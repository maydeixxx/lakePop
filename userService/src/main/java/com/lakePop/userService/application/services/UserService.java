package com.lakePop.userService.application.services;

import com.lakePop.userService.api.models.OrderDTO;
import com.lakePop.userService.api.models.UpdateResult;
import com.lakePop.userService.api.models.UserUpdateDTO;
import com.lakePop.userService.application.exceptions.ConflictException;
import com.lakePop.userService.application.exceptions.NewUserException;
import com.lakePop.userService.application.exceptions.UserUpdateException;
import com.lakePop.userService.application.exceptions.UsersNotFoundException;
import com.lakePop.userService.application.interfaces.IUserMapper;
import com.lakePop.userService.application.interfaces.IUserService;
import com.lakePop.userService.application.security.JwtService;
import com.lakePop.userService.domain.User;
import com.lakePop.userService.infrastructure.UserEntity;
import com.lakePop.userService.application.interfaces.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final IUserRepository repository;
    private final IUserMapper mapper;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserProducer userProducer;
    private final Map<String, CompletableFuture<OrderDTO>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public UpdateResult updateUser(String username, UserUpdateDTO userUpdateDTO) {
        UserEntity user = repository.findUserByUsername(username).orElseThrow(() -> new UsersNotFoundException(String.format("User[%s] not found", username)));
        String newToken = null;

        try {
            switch (userUpdateDTO.getField()) {
                case "username" -> {
                    if (repository.existsByUsername(userUpdateDTO.getNewUsername())) {
                        throw new ConflictException(String.format("Username [%s] already taken", userUpdateDTO.getNewUsername()));
                    }

                    user.setUsername(userUpdateDTO.getNewUsername());
                    newToken = jwtService.generateToken(mapper.userEntityToUser(user));
                }
                case "password" -> {
                    user.setPassword(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
                    newToken = jwtService.generateToken(mapper.userEntityToUser(user));
                }
                case "email" -> {
                    if (repository.existsByEmail(userUpdateDTO.getNewEmail())) {
                        throw new ConflictException(String.format("Email [%s] already taken", userUpdateDTO.getNewEmail()));
                    }

                    user.setEmail(userUpdateDTO.getNewEmail());
                }
                case "orders" -> {
                    List<Long> orders = user.getOrders();
                    orders.add(userUpdateDTO.getOrderId());
                    user.setOrders(orders);
                }
                default ->
                        throw new IllegalArgumentException("unknown field to update [" + userUpdateDTO.getField() + "]");
            }

        } catch (Exception e) {
            throw new UserUpdateException(String.format("Error while updating user %s. Error: %s", username, e.getMessage()));
        }
        return new UpdateResult(userUpdateDTO.getField() + " updated", newToken);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = repository.findAll().stream().map(mapper::userEntityToUser).toList();

        if (users.isEmpty()) {
            throw new UsersNotFoundException("users not found");
        }

        return users;
    }

    @Override
    public User findUserById(Long id) {
        return mapper.userEntityToUser(repository.findUserById(id).orElseThrow(() -> new UsersNotFoundException("User by id [" + id + "] not found")));
    }

    @Override
    public User findUserByEmail(String email) {
        return mapper.userEntityToUser(repository.findUserByEmail(email).orElseThrow(() -> new UsersNotFoundException(String.format("User by email [ %s ] not found", email))));
    }

    @Override
    public User findUserByUsername(String username) {
        return mapper.userEntityToUser(repository.findUserByUsername(username).orElseThrow(() -> new UsersNotFoundException(String.format("User %s not found", username))));
    }

    @Override
    public void createUser(User user) {
        try {
            repository.save(mapper.userToUserEntity(user));
        } catch (Exception e) {
            throw new NewUserException(String.format("Error while saving user. Error: %s", e.getMessage()));
        }
        log.info("User was successfully saved [ {} ]", user);
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            repository.delete(repository.findUserById(id).orElseThrow(() -> new UsersNotFoundException(String.format("User by id %s not found", id))));
        } catch (Exception e) {
            throw new ConflictException(String.format("Error while deleting user %s", id));
        } finally {
            log.info("User was successfully deleted {}", id);
        }
    }

    @Override
    @Transactional
    public void addOrder(String username, Long orderId) {
        UserEntity userByUsername = repository.findUserByUsername(username).orElseThrow(() -> new UsersNotFoundException(String.format("User %s not found", username)));
        List<Long> orders = userByUsername.getOrders();

        if (orders.contains(orderId)) {
            throw new ConflictException(String.format("Order [ %s ] already exists", orderId));
        }

        orders.add(orderId);
        userByUsername.setOrders(orders);
    }

    @Override
    @Transactional
    public void removeOrder(String username, Long orderId) {
        UserEntity user = repository.findUserByUsername(username).orElseThrow(() -> new UsersNotFoundException(String.format("User %s not found", username)));
        List<Long> orders = user.getOrders();

        if (!orders.contains(orderId)) {
            throw new ConflictException(String.format("User dont have order %s", orderId));
        }

        orders.removeIf(order -> order.equals(orderId));
    }

    @Override
    public List<Long> getOrderIds(String username) {
        User user = findUserByUsername(username);
        return user.getOrders();
    }

    @Override
    public OrderDTO getOrderInfoById(Long id) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<OrderDTO> request = new CompletableFuture<>();
        pendingRequests.put(requestId, request);
        userProducer.requestOrderBody(requestId, id);

        OrderDTO orderDTO;
        try {
            orderDTO = request.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Catched interruptedException: {}", e.getMessage());
            pendingRequests.remove(requestId);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            log.error("Catched executionException: {}", e.getMessage());
            pendingRequests.remove(requestId);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log.error("Catched timeoutException: {}", e.getMessage());
            pendingRequests.remove(requestId);
            throw new RuntimeException(e);
        }

        return orderDTO;
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "responseOrderId", partitions = {"0"}),
            groupId = "userService",
            containerFactory = "orderDTOConcurrentKafkaListenerContainerFactory"
    )
    public void handleOrderBody(ConsumerRecord<String, OrderDTO> record) {
        String requestId = record.key();
        OrderDTO order = record.value();

        if (requestId == null) {
            log.error("Request id is null");
        }

        if (order == null) {
            log.error("Received order is null");
        }

        CompletableFuture<OrderDTO> orderDTOCompletableFuture = pendingRequests.get(requestId);

        if (orderDTOCompletableFuture != null) {
            orderDTOCompletableFuture.complete(order);
        } else {
            log.error("Didnt receive order");
        }

    }
}