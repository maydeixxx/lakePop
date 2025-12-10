package com.lakePop.userService.api.models;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String field;

    private Long id;

    private String newUsername;

    private String newPassword;

    private String newEmail;

}
