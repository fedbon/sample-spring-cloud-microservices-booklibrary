package ru.fedbon.securityservice.exception;


import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
