package com.lakePop.userService.application;

import com.lakePop.userService.api.models.UserUpdateDTO;
import com.lakePop.userService.application.interfaces.IUserMapper;
import com.lakePop.userService.application.interfaces.IUserService;
import com.lakePop.userService.domain.User;
import com.lakePop.userService.infrastructure.UserEntity;
import com.lakePop.userService.application.interfaces.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final IUserRepository repository;
    private final IUserMapper mapper;

    @Override
    @Transactional
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        UserEntity userEntity = repository.findUserById(userUpdateDTO.getId()).orElseThrow(() -> new NullPointerException("Users not found"));
        User user = mapper.userEntityToUser(userEntity);
        try {
            switch (userUpdateDTO.getField()) {
                case "username" -> user.setUsername(userUpdateDTO.getNewUsername());
                case "password" -> user.setPassword(userUpdateDTO.getNewPassword());
                case "email" -> user.setEmail(userUpdateDTO.getNewEmail());
                default -> throw new IllegalArgumentException("unknown field to update [" + userUpdateDTO.getField() + "]");
            }

            repository.saveAndFlush(mapper.userToUserEntity(user));
        } catch (Exception e) {
            log.error("Error while updating user {}. Error: {}", userUpdateDTO.getId(), e.getMessage());
        }
        log.info("User was successfully updated");
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findAll().stream().map(mapper::userEntityToUser).toList();
    }

    @Override
    public User findUserById(Long id) {
        return mapper.userEntityToUser(repository.findUserById(id).orElseThrow(() -> new NullPointerException("User by id [" + id + "] not found")));
    }

    @Override
    public User findUserByEmail(String email) {
        return mapper.userEntityToUser(repository.findUserByEmail(email));
    }

    @Override
    public void createUser(User user) {
        try {
            repository.save(mapper.userToUserEntity(user));
        } catch (Exception e) {
            log.error("Error while saving user. Error: {}", e.getMessage());
        }
        log.info("User was successfully saved [ {} ]", user);
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            User userById = findUserById(id);
            repository.delete(mapper.userToUserEntity(userById));
        } catch (Exception e) {
            log.error("Error while deleting user {}", id);
        } finally {
            log.info("User was successfully deleted {}", id);
        }
    }
}
