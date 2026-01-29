package com.lakePop.userService.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateResult {

    String message;
    String newToken;

}
