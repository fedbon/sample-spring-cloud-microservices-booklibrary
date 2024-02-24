package ru.fedbon.commentservice.constants;

public class PathConstants {

    public static final String API_V1 = "/api/v1";

    public static final String BOOKS = "/books";

    public static final String BOOK_ID = "/{id}";

    public static final String API_V1_BOOKS = API_V1 + BOOKS;

    public static final String ID = "id";

    public static final String CREATED_AT = "createdAt";

    public static final String COMMENTS = "/comments";

    public static final String COUNT_COMMENTS_BY_USER_ID = "/user/{id}/count";

    public static final String COMMENTS_BY_BOOK_ID = "/book/{id}";

    public static final String COMMENTS_BY_USER_ID = "/user/{id}";

    public static final String API_V1_COMMENTS = API_V1 + COMMENTS;

    private PathConstants() {

    }
}
