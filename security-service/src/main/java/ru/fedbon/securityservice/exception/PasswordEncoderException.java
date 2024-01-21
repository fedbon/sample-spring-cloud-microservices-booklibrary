package ru.fedbon.securityservice.exception;


import lombok.Getter;

@Getter
public class PasswordEncoderException extends RuntimeException {

    private final String errorCode;

    public PasswordEncoderException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
