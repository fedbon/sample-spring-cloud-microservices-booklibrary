package ru.fedbon.voteservice.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }
}
