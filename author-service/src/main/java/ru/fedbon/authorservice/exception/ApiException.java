package ru.fedbon.authorservice.exception;


import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
