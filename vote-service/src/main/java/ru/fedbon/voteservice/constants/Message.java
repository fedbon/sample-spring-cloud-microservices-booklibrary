package ru.fedbon.voteservice.constants;

public class Message {

    public static final String RECEIVED_REQUEST_TO_VOTE_AUTHOR_BY_USER_ID = "Received request to handle vote " +
            "author for user ID: {}";

    public static final String SET_IS_ENABLED_FOR_EXISTING_AUTHOR_VOTE = "Setting isEnabled for existing " +
            "vote author (ID: {}) to: {}";

    public static final String VOTE_UPDATED = "Updated existing vote author: {}";

    public static final String VOTE_CREATED = "No existing vote author found. " +
            "Created and saved new vote author for request: {}";

    public static final String OFFSET_SENT = "sent {} offset : {}";

    public static final String VOTE_SENT = "Sent vote author message: {}";

    private Message() {

    }
}
