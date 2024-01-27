package ru.fedbon.gatewayserver.exception;


public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
