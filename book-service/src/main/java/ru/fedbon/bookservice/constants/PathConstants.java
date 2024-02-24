package ru.fedbon.bookservice.constants;

public class PathConstants {

    public static final String API_V1 = "/api/v1";

    public static final String USER_PARAM = "user";

    public static final String ID = "id";

    public static final String CREATED_AT = "createdAt";

    public static final String POSITIVE = "positive";

    public static final String NEGATIVE = "negative";

    public static final String AUTHORS = "/authors";

    public static final String AUTHOR_PARAM = "author";

    public static final String ORDER_PARAM = "order";

    public static final String FILTER_PARAM = "filter";

    public static final String DESC_PARAM = "desc";

    public static final String RATING_VALUE = "rating";

    public static final String TRUE_VALUE = "true";

    public static final String AUTHOR_ID = "/{id}";

    public static final String API_V1_AUTHORS = API_V1 + AUTHORS;

    public static final String BOOKS = "/books";

    public static final String COUNT_VOTES_BY_BOOK_ID = "/user/{id}/count";

    public static final String BOOK_ID = "/{id}";

    public static final String API_V1_BOOKS = API_V1 + BOOKS;

    public static final String GENRES = "/genres";

    public static final String GENRE_PARAM = "genre";

    public static final String API_V1_GENRES = API_V1 + GENRES;

    public static final String COMMENTS = "/comments";

    public static final String COMMENTS_BY_BOOK_ID = "/book/{id}";

    public static final String API_V1_COMMENTS = API_V1 + COMMENTS;

    private PathConstants() {

    }
}
