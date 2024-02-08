package ru.fedbon.commentservice.exception;

public class RandomQuoteApiException extends RuntimeException {
    public RandomQuoteApiException(String message) {
        super(message);
    }

    public RandomQuoteApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
