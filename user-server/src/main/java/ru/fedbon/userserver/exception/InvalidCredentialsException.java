package ru.fedbon.userserver.exception;


public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
