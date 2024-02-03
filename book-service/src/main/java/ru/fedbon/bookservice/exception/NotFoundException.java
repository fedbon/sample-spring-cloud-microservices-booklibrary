package ru.fedbon.bookservice.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }
}
