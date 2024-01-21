package ru.fedbon.securityservice.mapper;

import ru.fedbon.securityservice.dto.UserDto;
import ru.fedbon.securityservice.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto map(User user);

    @InheritInverseConfiguration
    User map(UserDto dto);
}