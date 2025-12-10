package com.lakePop.userService.application.interfaces;

import com.lakePop.userService.infrastructure.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IRoleRepository extends JpaRepository<Role, Integer> {

    Role findRoleByRoleName(String roleName);

}
