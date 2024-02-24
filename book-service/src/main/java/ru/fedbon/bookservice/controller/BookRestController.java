package ru.fedbon.bookservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.bookservice.dto.AuthorResponseDto;
import ru.fedbon.bookservice.dto.BookWithAuthorInfoAndCommentsDto;
import ru.fedbon.bookservice.dto.BookWithAuthorInfoDto;
import ru.fedbon.bookservice.dto.CommentResponseDto;
import ru.fedbon.bookservice.exception.NotFoundException;
import ru.fedbon.bookservice.mapper.BookMapper;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.repository.BookRepository;

import static ru.fedbon.bookservice.constants.AppConstants.CIRCUIT_BREAKER_AUTHOR_SERVICE;
import static ru.fedbon.bookservice.constants.AppConstants.CIRCUIT_BREAKER_COMMENT_SERVICE;
import static ru.fedbon.bookservice.constants.ErrorMessage.AUTHOR_INFORMATION_NOT_AVAILABLE_MESSAGE;
import static ru.fedbon.bookservice.constants.ErrorMessage.BOOKS_NOT_FOUND;
import static ru.fedbon.bookservice.constants.ErrorMessage.BOOK_NOT_FOUND;
import static ru.fedbon.bookservice.constants.ErrorMessage.COMMENTS_INFORMATION_NOT_AVAILABLE_MESSAGE;
import static ru.fedbon.bookservice.constants.Message.COUNT_VOTES_BY_BOOK_ID_MESSAGE;
import static ru.fedbon.bookservice.constants.Message.ENRICHED_BOOK_WITH_AUTHOR_INFO;
import static ru.fedbon.bookservice.constants.Message.ENRICHED_BOOK_WITH_AUTHOR_INFO_AND_COMMENTS;
import static ru.fedbon.bookservice.constants.Message.RECEIVED_COMMENTS_MESSAGE;
import static ru.fedbon.bookservice.constants.Message.SUCCESSFULLY_RECEIVED_AUTHOR_INFORMATION_MESSAGE;
import static ru.fedbon.bookservice.constants.Message.SUCCESSFULLY_RETRIEVED_BOOKS_BY_AUTHOR_ID_MESSAGE;
import static ru.fedbon.bookservice.constants.Message.SUCCESSFULLY_RETRIEVED_BOOKS_MESSAGE;
import static ru.fedbon.bookservice.constants.Message.SUCCESSFULLY_RETRIEVED_BOOK_MESSAGE;
import static ru.fedbon.bookservice.constants.PathConstants.API_V1_AUTHORS;
import static ru.fedbon.bookservice.constants.PathConstants.API_V1_BOOKS;
import static ru.fedbon.bookservice.constants.PathConstants.API_V1_COMMENTS;
import static ru.fedbon.bookservice.constants.PathConstants.AUTHOR_ID;
import static ru.fedbon.bookservice.constants.PathConstants.AUTHOR_PARAM;
import static ru.fedbon.bookservice.constants.PathConstants.BOOK_ID;
import static ru.fedbon.bookservice.constants.PathConstants.COMMENTS_BY_BOOK_ID;
import static ru.fedbon.bookservice.constants.PathConstants.COUNT_VOTES_BY_BOOK_ID;
import static ru.fedbon.bookservice.constants.PathConstants.CREATED_AT;
import static ru.fedbon.bookservice.constants.PathConstants.DESC_PARAM;
import static ru.fedbon.bookservice.constants.PathConstants.FILTER_PARAM;
import static ru.fedbon.bookservice.constants.PathConstants.GENRE_PARAM;
import static ru.fedbon.bookservice.constants.PathConstants.ID;
import static ru.fedbon.bookservice.constants.PathConstants.NEGATIVE;
import static ru.fedbon.bookservice.constants.PathConstants.ORDER_PARAM;
import static ru.fedbon.bookservice.constants.PathConstants.POSITIVE;
import static ru.fedbon.bookservice.constants.PathConstants.RATING_VALUE;
import static ru.fedbon.bookservice.constants.PathConstants.TRUE_VALUE;
import static ru.fedbon.bookservice.constants.PathConstants.USER_PARAM;
import static ru.fedbon.bookservice.constants.WebClientConstants.AUTHOR_SERVICE;
import static ru.fedbon.bookservice.constants.WebClientConstants.COMMENT_SERVICE;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_BOOKS)
public class BookRestController {

    private final BookRepository bookRepository;

    private final ReactiveCircuitBreakerFactory cbFactory;

    private final BookMapper bookMapper;

    private final WebClient.Builder webClientBuilder;

    @GetMapping(COUNT_VOTES_BY_BOOK_ID)
    public Mono<Long> countBooksVotedByUserId(@PathVariable(name = ID) String userId) {
        return bookRepository.findAll()
                .filter(book -> book.getVoteByUserList().stream()
                        .anyMatch(vote -> vote.getUserId().equals(userId)))
                .count()
                .doOnNext(count -> log.info(COUNT_VOTES_BY_BOOK_ID_MESSAGE, userId, count))
                .switchIfEmpty(Mono.error(new NotFoundException(BOOKS_NOT_FOUND + userId)));
    }

    @GetMapping
    public Flux<BookWithAuthorInfoDto> handleGetAll(
            @RequestParam(name = ORDER_PARAM, defaultValue = RATING_VALUE) String order,
            @RequestParam(name = DESC_PARAM, defaultValue = TRUE_VALUE) boolean desc) {
        return bookRepository.findAll()
                .flatMap(this::enrichBookWithAuthorInfo)
                .sort((bookDto1, bookDto2) -> compareBooks(bookDto1, bookDto2, order, desc))
                .doOnNext(bookDto -> log.info(SUCCESSFULLY_RETRIEVED_BOOKS_MESSAGE, bookDto));
    }

    @GetMapping(params = GENRE_PARAM)
    public Flux<BookWithAuthorInfoDto> handleGetAllByGenreId(
            @RequestParam(name = GENRE_PARAM) String genreId,
            @RequestParam(name = ORDER_PARAM, defaultValue = RATING_VALUE) String order,
            @RequestParam(name = DESC_PARAM, defaultValue = TRUE_VALUE) boolean desc) {
        return bookRepository.findAllByGenreId(genreId)
                .flatMap(this::enrichBookWithAuthorInfo)
                .sort((bookDto1, bookDto2) -> compareBooks(bookDto1, bookDto2, order, desc))
                .doOnNext(bookDto -> log.info(SUCCESSFULLY_RETRIEVED_BOOKS_MESSAGE, bookDto));
    }

    @GetMapping(value = BOOK_ID)
    public Mono<BookWithAuthorInfoAndCommentsDto> handleGetById(@PathVariable(value = ID) String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(BOOK_NOT_FOUND + id)))
                .flatMap(book -> enrichBookWithAuthorInfoAndComments(bookMapper
                        .mapToBookWithAuthorsAndCommentsDto(book)))
                .doOnNext(bookDto -> log.info(SUCCESSFULLY_RETRIEVED_BOOK_MESSAGE, bookDto));
    }

    @GetMapping(params = AUTHOR_PARAM)
    public Flux<BookWithAuthorInfoDto> handleGetAllByAuthorId(
            @RequestParam(value = AUTHOR_PARAM) String authorId,
            @RequestParam(name = ORDER_PARAM, defaultValue = RATING_VALUE) String order,
            @RequestParam(name = DESC_PARAM, defaultValue = TRUE_VALUE) boolean desc) {
        return bookRepository.findAllByAuthorId(authorId)
                .flatMap(this::enrichBookWithAuthorInfo)
                .sort((bookDto1, bookDto2) -> compareBooks(bookDto1, bookDto2, order, desc))
                .doOnNext(bookDto -> log.info(SUCCESSFULLY_RETRIEVED_BOOKS_BY_AUTHOR_ID_MESSAGE, authorId));
    }

    @GetMapping(params = USER_PARAM)
    public Flux<BookWithAuthorInfoAndCommentsDto> handleGetAllByUserId(
            @RequestParam(name = USER_PARAM) String userId,
            @RequestParam(name = FILTER_PARAM, required = false) String filter) {
        return bookRepository.findAll()
                .filter(book -> shouldIncludeBook(book, userId, filter))
                .flatMap(book -> enrichBookWithAuthorInfoAndComments(bookMapper
                        .mapToBookWithAuthorsAndCommentsDto(book)))
                .sort((book1, book2) -> compareBooksByRatingAndVote(book1, book2, filter));
    }

    private Mono<BookWithAuthorInfoDto> enrichBookWithAuthorInfo(Book book) {
        return fetchAuthorInfo(book.getAuthorId())
                .map(authorDto -> {
                    var bookWithAuthorsDto = bookMapper.mapToBookWithAuthorsDto(book);
                    bookWithAuthorsDto.setAuthorName(authorDto.getName());
                    return bookWithAuthorsDto;
                })
                .doOnNext(bookDto -> log.info(ENRICHED_BOOK_WITH_AUTHOR_INFO, bookDto));
    }

    private Mono<BookWithAuthorInfoAndCommentsDto> enrichBookWithAuthorInfoAndComments(
            BookWithAuthorInfoAndCommentsDto bookDto) {
        return fetchAuthorInfo(bookDto.getAuthorId())
                .flatMap(authorDto -> {
                    var commentsFlux = fetchComments(bookDto.getId());
                    return commentsFlux.collectList()
                            .map(comments -> {
                                bookDto.setAuthorName(authorDto.getName());
                                bookDto.setComments(comments);
                                bookDto.setCommentsCount(comments.size());
                                return bookDto;
                            });
                })
                .doOnNext(dto -> log.info(ENRICHED_BOOK_WITH_AUTHOR_INFO_AND_COMMENTS, dto));
    }

    private Mono<AuthorResponseDto> fetchAuthorInfo(String authorId) {
        return webClientBuilder.build()
                .get()
                .uri(AUTHOR_SERVICE + API_V1_AUTHORS + AUTHOR_ID, authorId)
                .retrieve()
                .bodyToMono(AuthorResponseDto.class)
                .doOnNext(authorDto -> log.info(SUCCESSFULLY_RECEIVED_AUTHOR_INFORMATION_MESSAGE, authorDto))
                .transform(it -> cbFactory.create(CIRCUIT_BREAKER_AUTHOR_SERVICE)
                        .run(it, throwable -> Mono.just(new AuthorResponseDto(null,
                                AUTHOR_INFORMATION_NOT_AVAILABLE_MESSAGE, null))));

    }

    private Flux<CommentResponseDto> fetchComments(String bookId) {
        return webClientBuilder.build()
                .get()
                .uri(COMMENT_SERVICE + API_V1_COMMENTS + COMMENTS_BY_BOOK_ID, bookId)
                .retrieve()
                .bodyToFlux(CommentResponseDto.class)
                .transform(it -> cbFactory.create(CIRCUIT_BREAKER_COMMENT_SERVICE)
                        .run(it, throwable -> Flux.just(new CommentResponseDto(null,
                                COMMENTS_INFORMATION_NOT_AVAILABLE_MESSAGE, null, null,
                                null, null, null))))
                .doOnNext(commentDto -> log.info(RECEIVED_COMMENTS_MESSAGE, commentDto));
    }

    private boolean shouldIncludeBook(Book book, String userId, String filter) {
        boolean hasUserVote = book.getVoteByUserList().stream()
                .anyMatch(vote -> vote.getUserId().equals(userId));
        if (filter != null && filter.equalsIgnoreCase(POSITIVE)) {
            return hasUserVote && book.getVoteByUserList().stream()
                    .anyMatch(vote -> vote.getUserId().equals(userId) && vote.isPositive());
        } else if (filter != null && filter.equalsIgnoreCase(NEGATIVE)) {
            return hasUserVote && book.getVoteByUserList().stream()
                    .anyMatch(vote -> vote.getUserId().equals(userId) && !vote.isPositive());
        } else {
            return hasUserVote;
        }
    }

    private int compareBooksByRatingAndVote(BookWithAuthorInfoAndCommentsDto bookDto1,
                                            BookWithAuthorInfoAndCommentsDto bookDto2, String order) {
        int ratingComparison = Double.compare(bookDto2.getRating(), bookDto1.getRating());
        if (ratingComparison != 0) {
            return ratingComparison;
        } else {
            return compareBooksByVote(bookDto1, bookDto2, order);
        }
    }

    private int compareBooks(BookWithAuthorInfoDto bookDto1, BookWithAuthorInfoDto bookDto2,
                             String order, boolean desc) {
        if (CREATED_AT.equalsIgnoreCase(order)) {
            return compareByCreatedAt(bookDto1, bookDto2, desc);
        } else {
            return compareByRating(bookDto1, bookDto2, desc);
        }
    }

    private int compareBooksByVote(BookWithAuthorInfoAndCommentsDto bookDto1,
                                   BookWithAuthorInfoAndCommentsDto bookDto2, String order) {
        if (POSITIVE.equalsIgnoreCase(order)) {
            return Integer.compare(bookDto2.getPositiveVotesCount(), bookDto1.getPositiveVotesCount());
        } else if (NEGATIVE.equalsIgnoreCase(order)) {
            return Integer.compare(bookDto2.getNegativeVotesCount(), bookDto1.getNegativeVotesCount());
        } else {
            return 0;
        }
    }

    private int compareByCreatedAt(BookWithAuthorInfoDto bookDto1,
                                   BookWithAuthorInfoDto bookDto2, boolean desc) {
        if (desc) {
            return bookDto2.getCreatedAt().compareTo(bookDto1.getCreatedAt());
        } else {
            return bookDto1.getCreatedAt().compareTo(bookDto2.getCreatedAt());
        }
    }

    private int compareByRating(BookWithAuthorInfoDto bookDto1,
                                BookWithAuthorInfoDto bookDto2, boolean desc) {
        if (desc) {
            return Double.compare(bookDto2.getRating(), bookDto1.getRating());
        } else {
            return Double.compare(bookDto1.getRating(), bookDto2.getRating());
        }
    }
}