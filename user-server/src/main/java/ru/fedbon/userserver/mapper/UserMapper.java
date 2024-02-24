package ru.fedbon.userserver.mapper;

import ru.fedbon.userserver.dto.AccountDto;
import ru.fedbon.userserver.dto.UserDto;
import ru.fedbon.userserver.dto.security.RegistrationRequest;
import ru.fedbon.userserver.dto.security.RegistrationResponse;
import ru.fedbon.userserver.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapRegistrationRequestToUser(RegistrationRequest registrationRequest);

    RegistrationResponse mapUserToRegistrationResponse(User user);

    AccountDto mapUserToAccountDto(User user);

    UserDto mapUserToUserDto(User user);
}