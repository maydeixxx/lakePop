package com.lakePop.userService.application;

import com.lakePop.userService.api.models.UserUpdateDTO;
import com.lakePop.userService.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface IUserService {

    void updateUser(UserUpdateDTO userUpdateDTO);

    List<User> findAllUsers();

    User findUserById(Long id);

    User findUserByEmail(String email);

    void createUser(User user);

    void deleteUserById(Long id);

}
