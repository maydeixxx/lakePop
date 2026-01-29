package com.lakePop.userService.api.models;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.processing.Pattern;

@Data
@Builder
public class UserUpdateDTO {

    private String field;

    private String currentPassword;

    private String newUsername;

    private String newPassword;

    private String newEmail;

    private Long orderId;

}
