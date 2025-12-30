package com.lakePop.userService.application.interfaces;
import com.lakePop.userService.api.models.UserDTO;
import com.lakePop.userService.domain.User;
import com.lakePop.userService.infrastructure.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    User userEntityToUser(UserEntity userEntity);

    UserEntity userToUserEntity(User user);

    @Mapping(target = "password", ignore = true)
    UserDTO userToUserDTO(User user);

    User userDTOtoUser(UserDTO userDTO);

}
