package ru.fedbon.userserver.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }
}
