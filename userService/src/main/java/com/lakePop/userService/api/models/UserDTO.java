package com.lakePop.userService.api.models;

import com.lakePop.userService.infrastructure.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String email;

    private Set<Role> roles; //Admin, User

}
