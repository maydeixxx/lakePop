package com.lakePop.userService.api.controllers;

import com.lakePop.userService.api.models.UserAuthDTO;
import com.lakePop.userService.api.models.UserRegDTO;
import com.lakePop.userService.application.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/userService/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserAuthDTO userData) {
        String token = authService.authenticate(userData);

        if (token == null) {
            return ResponseEntity.badRequest().body("Error while login");
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createUser(@RequestBody UserRegDTO user) {
        try {
            authService.regUser(user);
            return ResponseEntity.ok().body("User successfully saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

}
