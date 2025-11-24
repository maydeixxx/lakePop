package com.lakePop.userService.application.auth.config;

import com.lakePop.userService.application.interfaces.IRoleRepository;
import com.lakePop.userService.infrastructure.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
public class StartConfig {

    private final IRoleRepository repository;

    @Bean
    public String initRoles() {
        if (repository.findRoleByRoleName("Admin") == null) {
            repository.save(new Role(null, "Admin"));
        }
        return "";
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
