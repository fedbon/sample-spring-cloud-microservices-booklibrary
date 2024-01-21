package ru.fedbon.securityservice.dto.security;

import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class ValidationResult {

    private final Claims claims;

    private final String token;
}
