package ru.fedbon.authorservice.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }
}
