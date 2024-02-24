package ru.fedbon.userserver.constants;

public class PathConstants {

    public static final String BEARER_SERVER_WEB_EXCHANGE_MATCHERS = "/**";

    public static final String API_V1 = "/api/v1";

    public static final String AUTH = "/auth";

    public static final String SIGN_UP = "/signup";

    public static final String SIGN_IN = "/signin";

    public static final String TOKEN_PARAM = "token";

    public static final String VALIDATE = "/validate";

    public static final String API_V1_AUTH = API_V1 + AUTH;

    public static final String USER = "/user";

    public static final String MY_ACCOUNT = "/my";

    public static final String USER_ID = "/{id}";

    public static final String API_V1_USER = API_V1 + USER;

    public static final String AUTHORS = "/authors";

    public static final String COUNT_VOTES_BY_AUTHOR_ID = "/user/{id}/count";

    public static final String API_V1_AUTHORS = API_V1 + AUTHORS;

    public static final String BOOKS = "/books";

    public static final String COUNT_VOTES_BY_BOOK_ID = "/user/{id}/count";

    public static final String API_V1_BOOKS = API_V1 + BOOKS;

    public static final String COMMENTS = "/comments";

    public static final String COUNT_COMMENTS_BY_USER_ID = "/user/{id}/count";

    public static final String API_V1_COMMENTS = API_V1 + COMMENTS;

    private PathConstants() {

    }
}
