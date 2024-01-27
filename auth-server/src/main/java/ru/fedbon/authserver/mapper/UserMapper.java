package ru.fedbon.authserver.mapper;

import ru.fedbon.authserver.dto.UserDto;
import ru.fedbon.authserver.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto map(User user);

    @InheritInverseConfiguration
    User map(UserDto dto);
}