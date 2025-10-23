package com.lakePop.userService.api.models;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String email;

}
