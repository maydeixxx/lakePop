package com.lakePop.userService.application.config;

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
        if (repository.findRoleByRoleName("ADMIN") == null) {
            repository.save(new Role(null, "ADMIN"));
        }
        if (repository.findRoleByRoleName("USER") == null) {
            repository.save(new Role(null, "USER"));
        }
        return "";
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
