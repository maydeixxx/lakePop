package com.lakePop.userService.api.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDTO {

    private String field;

    private Long id;

    private String newUsername;

    private String newPassword;

    private String newEmail;

    private Long orderId;

}
