package com.lakePop.userService.application.auth;

import com.lakePop.userService.api.models.UserAuthDTO;
import com.lakePop.userService.api.models.UserDTO;
import com.lakePop.userService.application.JwtService;
import com.lakePop.userService.application.UserService;
import com.lakePop.userService.application.interfaces.IUserMapper;
import com.lakePop.userService.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final IUserMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public void regUser(UserDTO userData) {
        User userByEmail = userService.findUserByEmail(userData.getEmail());

        if (userByEmail != null) {
            throw new IllegalArgumentException(String.format("Пользователь с email [%s] уже существует", userData.getEmail()));
        }

        String encodedPassword = passwordEncoder.encode(userData.getPassword());
        userData.setPassword(encodedPassword);

        userService.createUser(mapper.userDTOtoUser(userData));

        log.info("Зарегистрирован пользователь {}", userData);
    }

    public String authenticate(UserAuthDTO userData) {

        User userByEmail = userService.findUserByEmail(userData.getEmail());

        if (userByEmail == null) {
            throw new IllegalArgumentException(String.format("Пользователь с email [%s] не найден", userData.getEmail()));
        }

        if (!passwordEncoder.matches(userData.getPassword(), userByEmail.getPassword())) {
            throw new IllegalArgumentException(String.format("Пароль для email [%s] неправильный", userData.getEmail()));
        }

        return jwtService.generateToken(userByEmail);
    }

}
