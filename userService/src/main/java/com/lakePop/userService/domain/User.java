package com.lakePop.userService.domain;

import com.lakePop.userService.infrastructure.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String type;

    private Set<Role> roles; //Admin, User

    private Integer countOfSold; //count of sold items

    private List<Long> orders;

}