package ru.fedbon.authorservice.constants;

public class Message {

    public static final String COUNT_VOTES_BY_AUTHOR_ID_MESSAGE = "Counted votes for author with id {}: {}";

    public static final String SUCCESSFULLY_RETRIEVED_AUTHOR_BY_ID_MESSAGE = "Successfully retrieved author with id: {}";

    public static final String AUTHOR_VOTED_BY_USER_ID_FOUND_MESSAGE = "Author voted by user with id {} found: {}";

    public static final String CONSUMER_RECORD = "Received key={}, value={} from topic={}, offset={}";

    public static final String SUCCESSFULLY_CONSUMED_MESSAGE = "Successfully consumed {}={}";

    private Message() {

    }
}
