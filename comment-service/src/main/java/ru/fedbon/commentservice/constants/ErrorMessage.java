package ru.fedbon.commentservice.constants;

public class ErrorMessage {

    public static final String NOT_FOUND = "NOT_FOUND";

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String COMMENTS_NOT_FOUND_BY_USER_ID = "No comments found by user with id: ";

    public static final String COMMENTS_NOT_FOUND_BY_BOOK_ID = "No comments found by book with id: ";

    public static final String AUTHOR_INFORMATION_NOT_AVAILABLE_MESSAGE = "Author information not available";

    public static final String BOOK_INFORMATION_NOT_AVAILABLE_MESSAGE = "Book information not available";

    private ErrorMessage() {

    }
}
