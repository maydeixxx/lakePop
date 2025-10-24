package com.lakePop.userService.domain;

import com.lakePop.userService.infrastructure.Role;
import lombok.Data;

import java.util.Set;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String type;

    private Set<Role> roles; //Admin, User

}