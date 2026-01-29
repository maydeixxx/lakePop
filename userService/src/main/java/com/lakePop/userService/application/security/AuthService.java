package com.lakePop.userService.application.security;

import com.lakePop.userService.api.models.UserAuthDTO;
import com.lakePop.userService.api.models.UserDTO;
import com.lakePop.userService.api.models.UserRegDTO;
import com.lakePop.userService.application.UserService;
import com.lakePop.userService.application.interfaces.IUserMapper;
import com.lakePop.userService.application.interfaces.IUserRepository;
import com.lakePop.userService.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public void regUser(UserRegDTO userData) {

        if (userService.existsByEmail(userData.getEmail())) {
            throw new IllegalArgumentException(
                    "User with email [%s] already exists".formatted(userData.getEmail())
            );
        }

        User user = User.builder()
                .username(userData.getUsername())
                .email(userData.getEmail())
                .password(passwordEncoder.encode(userData.getPassword()))
                .type("Buyer")
                .orders(new ArrayList<>())
                .build();

        userService.createUser(user);

        log.info("User was successfully registered {}", userData);
    }

    public String authenticate(UserAuthDTO userData) {

        User userByEmail = userService.findUserByEmail(userData.getEmail());

        if (userByEmail == null) {
            throw new IllegalArgumentException(String.format("User with email [%s] not found", userData.getEmail()));
        }

        if (!passwordEncoder.matches(userData.getPassword(), userByEmail.getPassword())) {
            throw new IllegalArgumentException(String.format("Password for [%s] is wrong", userData.getEmail()));
        }

        return jwtService.generateToken(userByEmail);
    }

}
