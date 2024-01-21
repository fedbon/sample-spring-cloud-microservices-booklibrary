package ru.fedbon.securityservice.dto;


import lombok.Builder;
import lombok.Data;
import ru.fedbon.securityservice.model.UserRole;


@Data
@Builder
public class UserValidationResponse {

    private Long id;

    private String username;

    private UserRole role;

    private String firstName;

    private String lastName;

    private String token;
}
