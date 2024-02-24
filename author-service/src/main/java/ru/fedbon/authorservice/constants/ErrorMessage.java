package ru.fedbon.authorservice.constants;

public class ErrorMessage {

    public static final String NOT_FOUND = "NOT_FOUND";

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String AUTHOR_NOT_FOUND = "No author found with id: ";

    public static final String AUTHORS_NOT_FOUND = "No authors found voted by user with id: ";

    public static final String CONSUMPTION_PROBLEM_MESSAGE = "Something bad happened while processing message: {}";

    private ErrorMessage() {

    }
}
