package com.lakePop.userService.api.controllers;

import com.lakePop.userService.api.models.*;
import com.lakePop.userService.application.interfaces.IUserMapper;
import com.lakePop.userService.application.services.UserService;
import com.lakePop.userService.application.security.AuthService;
import com.lakePop.userService.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/userService")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final IUserMapper mapper;

    @GetMapping("/all")
    public ResponseEntity<?> findAllUsers() {
        try {
            List<User> allUsers = userService.findAllUsers();
            return ResponseEntity.ok(allUsers.stream().map(mapper::userToUserDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> authenticateUser(@RequestBody UserAuthDTO userData) {
        String token = authService.authenticate(userData);

        if (token == null) {
            return ResponseEntity.badRequest().body("Error while login");
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRegDTO user) {
        try {
            authService.regUser(user);
            return ResponseEntity.ok().body("User successfully saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        try {
            UpdateResult result = userService.updateUser(username, userUpdateDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", result.getMessage());
            response.put("field", userUpdateDTO.getField());

            if (result.getNewToken() != null) {
                response.put("newToken", result.getNewToken());
                response.put("tokenType", "Bearer");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(mapper.userToUserDTO(userService.findUserById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while finding user by id [" + id + "]");
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok().body(mapper.userToUserDTO(userService.findUserByEmail(email)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while finding user by email [" + email + "]");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("Successfully deleted user by id [" + id + "]");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while deleting user by id [" + id + "]");
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<Long> orderIds = userService.getOrderIds(username);
            return ResponseEntity.ok(String.format("Your orders: %s", orderIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error in getting order ids");
        }
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrderBody(@PathVariable Long id) {
        try {
            OrderDTO order = userService.getOrderInfoById(id);
            return ResponseEntity.ok(String.format("Order %s: %s", id, order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error in getting order body");
        }
    }

    @PatchMapping("/removeOrder/{id}")
    public ResponseEntity<?> removeOrder(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userService.removeOrder(username, id);
            return ResponseEntity.ok(String.format("Order %s deleted", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error while deleting order");
        }
    }

}
