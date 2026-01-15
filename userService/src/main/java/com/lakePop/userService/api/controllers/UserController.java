package com.lakePop.userService.api.controllers;

import com.lakePop.userService.api.models.UserAuthDTO;
import com.lakePop.userService.api.models.UserDTO;
import com.lakePop.userService.api.models.UserRegDTO;
import com.lakePop.userService.api.models.UserUpdateDTO;
import com.lakePop.userService.application.interfaces.IUserMapper;
import com.lakePop.userService.application.UserService;
import com.lakePop.userService.application.security.AuthService;
import com.lakePop.userService.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userService")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final IUserMapper mapper;

    @GetMapping("/all")
    private ResponseEntity<?> findAllUsers() {
        try {
            List<User> allUsers = userService.findAllUsers();
            return ResponseEntity.ok(allUsers.stream().map(mapper::userToUserDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/token")
    private ResponseEntity<?> authenticateUser(@RequestBody UserAuthDTO userData) {
        String token = authService.authenticate(userData);

        if (token == null) {
            return ResponseEntity.badRequest().body("Error while login");
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/create")
    private ResponseEntity<?> createUser(@RequestBody UserRegDTO user) {
        try {
            authService.regUser(user);
            return ResponseEntity.ok().body("User successfully saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @PatchMapping("/update")
    private ResponseEntity<?> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            userService.updateUser(userUpdateDTO);
            return ResponseEntity.ok("User successfully updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/id/{id}")
    private ResponseEntity<?> findUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(mapper.userToUserDTO(userService.findUserById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while finding user by id [" + id + "]");
        }
    }

    @GetMapping("/email/{email}")
    private ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok().body(mapper.userToUserDTO(userService.findUserByEmail(email)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while finding user by email [" + email + "]");
        }
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("Successfully deleted user by id [" + id + "]");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while deleting user by id [" + id + "]");
        }
    }

}
