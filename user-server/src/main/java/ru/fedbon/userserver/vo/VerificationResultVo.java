package ru.fedbon.userserver.vo;

import io.jsonwebtoken.Claims;



public record VerificationResultVo(Claims claims, String token) {

}
