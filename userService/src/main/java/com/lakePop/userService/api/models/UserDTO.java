package com.lakePop.userService.api.models;

import com.lakePop.userService.infrastructure.Role;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@ToString(onlyExplicitlyIncluded = true)
public class UserDTO {

    private Long id;

    @ToString.Include
    private String username;

    private String password;

    @ToString.Include
    private String email;

    private Set<Role> roles; //Admin, User

    private Integer countOfSold; //count of sold items

    private List<String> orders;

}
