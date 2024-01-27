package ru.fedbon.authserver.vo;

import io.jsonwebtoken.Claims;



public record VerificationResultVo(Claims claims, String token) {

}
