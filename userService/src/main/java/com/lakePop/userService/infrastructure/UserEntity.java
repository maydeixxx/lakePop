package com.lakePop.userService.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;


@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    private String password;

    @Column(name = "email", unique = true)
    private String email;

    private String type; //buyer or shop

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles; //Admin, User

    private Integer countOfSold; //count of sold items

    @ElementCollection
    @CollectionTable(
            name = "users_orders",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private List<Long> orders;

}
