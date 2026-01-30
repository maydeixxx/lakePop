package com.lakePop.userService.application;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final IUserRepository repository;
    private final IUserMapper mapper;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UpdateResult updateUser(String username, UserUpdateDTO userUpdateDTO) {
        UserEntity userEntity = repository.findUserByUsername(username).orElseThrow(() -> new NullPointerException(String.format("User[%s] not found", username)));
        User user = mapper.userEntityToUser(userEntity);
        String newToken = null;

        try {
            switch (userUpdateDTO.getField()) {
                case "username" ->  {
                    if (repository.existsByUsername(userUpdateDTO.getNewUsername())) {
                        throw new ConflictException(String.format("Username [%s] already taken", userUpdateDTO.getNewUsername()));
                    }

                    user.setUsername(userUpdateDTO.getNewUsername());
                    newToken = jwtService.generateToken(user);
                }
                case "password" -> {
                    user.setPassword(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
                    newToken = jwtService.generateToken(user);
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

            repository.save(mapper.userToUserEntity(user));

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
            User userById = findUserById(id);
            repository.delete(mapper.userToUserEntity(userById));
        } catch (Exception e) {
            throw new ConflictException(String.format("Error while deleting user %s", id));
        } finally {
            log.info("User was successfully deleted {}", id);
        }
    }
}
