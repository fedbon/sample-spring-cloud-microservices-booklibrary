package ru.fedbon.voteservice.constants;

public class PathConstants {

    public static final String AUTH_USER_ID_HEADER = "X-auth-user-id";

    public static final String API_V1 = "/api/v1";

    public static final String VOTES = "/vote";

    public static final String VOTE_AUTHOR = "/author";

    public static final String API_V1_VOTES = API_V1 + VOTES;

    private PathConstants() {

    }
}
