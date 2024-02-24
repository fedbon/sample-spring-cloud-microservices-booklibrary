package ru.fedbon.userserver.constants;

public class ErrorMessage {

    public static final String NOT_FOUND = "NOT_FOUND";

    public static final String UNAUTHORIZED = "UNAUTHORIZED";

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String UNAUTHORIZED_WITH_MESSAGE = "IN securityWebFilterChain - unauthorized error: {}";

    public static final String ACCESS_DENIED = "IN securityWebFilterChain - access denied: {}";

    public static final String ACCOUNT_DISABLED = "Account is disabled";

    public static final String PASSWORD_INVALID = "Invalid password";

    public static final String USERNAME_INVALID = "Invalid username";

    public static final String TOKEN_EXPIRED_WITH_MESSAGE = "Expired token: {}";

    public static final String TOKEN_EXPIRED = "Expired token";

    public static final String TOKEN_INVALID_WITH_MESSAGE = "Invalid token: {}";

    public static final String TOKEN_INVALID = "Invalid token";

    public static final String FAILED_TO_FETCH_AUTHORS_BY_USER_ID = "Failed to fetch authors by user with id: {}";

    public static final String FAILED_TO_FETCH_BOOKS_BY_USER_ID = "Failed to fetch books by user with id: {}";

    public static final String FAILED_TO_FETCH_COMMENTS_BY_USER_ID = "Failed to fetch comments by user with id: {}";

    public static final String MISSED_AUTHORIZATION_HEADER = "Missed authorization header";

    public static final String USER_NOT_FOUND = "No user found with id: ";

    public static final String AUTHORS_NOT_FOUND_BY_USER_ID = "No authors found voted by user with id: ";

    public static final String BOOKS_NOT_FOUND_BY_USER_ID = "No books found voted by user with id: ";

    public static final String COMMENTS_NOT_FOUND_BY_USER_ID = "No comments found by user with id: ";

    private ErrorMessage() {

    }
}
