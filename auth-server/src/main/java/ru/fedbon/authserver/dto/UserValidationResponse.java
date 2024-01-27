package ru.fedbon.authserver.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserValidationResponse {

    private String id;

    private String role;
}
