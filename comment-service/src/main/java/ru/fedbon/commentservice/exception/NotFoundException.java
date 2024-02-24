package ru.fedbon.commentservice.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }
}
