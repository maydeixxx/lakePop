package com.lakePop.userService.api.models;

import lombok.Data;

@Data
public class UserAuthDTO {

    private final String email;

    private final String password;

}
