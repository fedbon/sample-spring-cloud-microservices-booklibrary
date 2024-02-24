package ru.fedbon.authorservice.constants;

public class PathConstants {

    public static final String API_V1 = "/api/v1";

    public static final String AUTHORS = "/authors";

    public static final String COUNT_VOTES_BY_AUTHOR_ID = "/user/{id}/count";

    public static final String AUTHOR_ID = "/{id}";

    public static final String AUTHORS_VOTED_BY_USER_ID = "/user/{id}";

    public static final String API_V1_AUTHORS = API_V1 + AUTHORS;

    private PathConstants() {

    }
}
