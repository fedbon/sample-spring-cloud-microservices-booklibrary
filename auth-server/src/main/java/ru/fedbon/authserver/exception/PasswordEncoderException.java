package ru.fedbon.authserver.exception;


import lombok.Getter;

@Getter
public class PasswordEncoderException extends RuntimeException {

    public PasswordEncoderException(String message) {
        super(message);
    }
}
