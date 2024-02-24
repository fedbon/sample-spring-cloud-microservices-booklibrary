package ru.fedbon.bookservice.constants;

public class Message {

    public static final String COUNT_VOTES_BY_BOOK_ID_MESSAGE = "Counted votes for book with id {}: {}";

    public static final String SUCCESSFULLY_RETRIEVED_BOOK_MESSAGE = "Successfully retrieved book: {}";

    public static final String SUCCESSFULLY_RETRIEVED_GENRES_MESSAGE = "Successfully retrieved genre: {}";

    public static final String SUCCESSFULLY_RETRIEVED_BOOKS_MESSAGE = "Successfully retrieved books: {}";

    public static final String SUCCESSFULLY_RETRIEVED_BOOKS_BY_AUTHOR_ID_MESSAGE = "Successfully retrieved " +
            "books by author id: {}";

    public static final String ENRICHED_BOOK_WITH_AUTHOR_INFO = "Enriched book with author info: {}";

    public static final String ENRICHED_BOOK_WITH_AUTHOR_INFO_AND_COMMENTS = "Enriched book with " +
            "author info and comments: {}";

    public static final String SUCCESSFULLY_RECEIVED_AUTHOR_INFORMATION_MESSAGE = "Received author information: {}";

    public static final String RECEIVED_COMMENTS_MESSAGE = "Received comments: {}";

    private Message() {

    }
}
