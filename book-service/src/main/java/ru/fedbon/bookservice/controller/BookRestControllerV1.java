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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookRestControllerV1 {

    private final BookRepository bookRepository;

    private final ReactiveCircuitBreakerFactory cbFactory;

    private final BookMapper bookMapper;

    private final WebClient.Builder webClientBuilder;

    @GetMapping("/user/{id}/count")
    public Mono<Long> countBooksVotedByUserId(@PathVariable(name = "id") String userId) {
        return bookRepository.findAll()
                .filter(book -> book.getVoteByUserList().stream()
                        .anyMatch(vote -> vote.getUserId().equals(userId)))
                .count()
                .doOnNext(count -> log.info("Counted books voted by user ID {}: {}", userId, count))
                .switchIfEmpty(Mono.error(new NotFoundException("No books voted by user")));
    }

    @GetMapping
    public Flux<BookWithAuthorInfoDto> handleGetAll(
            @RequestParam(name = "order", defaultValue = "rating") String order,
            @RequestParam(name = "desc", defaultValue = "true") boolean desc) {
        return bookRepository.findAll()
                .flatMap(this::enrichBookWithAuthorInfo)
                .sort((bookDto1, bookDto2) -> compareBooks(bookDto1, bookDto2, order, desc))
                .doOnNext(bookDto -> log.info("Returning book: {}", bookDto));
    }

    @GetMapping(params = "genre")
    public Flux<BookWithAuthorInfoDto> handleGetAllByGenreId(
            @RequestParam(name = "genre") String genreId,
            @RequestParam(name = "order", defaultValue = "rating") String order,
            @RequestParam(name = "desc", defaultValue = "true") boolean desc) {
        return bookRepository.findAllByGenreId(genreId)
                .flatMap(this::enrichBookWithAuthorInfo)
                .sort((bookDto1, bookDto2) -> compareBooks(bookDto1, bookDto2, order, desc))
                .doOnNext(bookDto -> log.info("Returning book: {}", bookDto));
    }

    @GetMapping(value = "/{id}")
    public Mono<BookWithAuthorInfoAndCommentsDto> handleGetById(@PathVariable(value = "id") String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Book not found")))
                .flatMap(book -> enrichBookWithAuthorInfoAndComments(bookMapper
                        .mapToBookWithAuthorsAndCommentsDto(book)))
                .doOnNext(bookDto -> log.info("Returning book: {}", bookDto));
    }

    @GetMapping(params = "author")
    public Flux<BookWithAuthorInfoDto> handleGetAllByAuthorId(
            @RequestParam(value = "author") String authorId,
            @RequestParam(name = "order", defaultValue = "rating") String order,
            @RequestParam(name = "desc", defaultValue = "true") boolean desc) {
        return bookRepository.findAllByAuthorId(authorId)
                .flatMap(this::enrichBookWithAuthorInfo)
                .sort((bookDto1, bookDto2) -> compareBooks(bookDto1, bookDto2, order, desc))
                .doOnNext(bookDto -> log.info("Books retrieved successfully for Author ID: {}", authorId));
    }

    @GetMapping(params = "user")
    public Flux<BookWithAuthorInfoAndCommentsDto> handleGetAllByUserId(
            @RequestParam(name = "user") String userId,
            @RequestParam(name = "sort", defaultValue = "positive") String sort) {
        return bookRepository.findAll()
                .filter(book -> shouldIncludeBook(book, userId, sort))
                .flatMap(book -> enrichBookWithAuthorInfoAndComments(bookMapper
                        .mapToBookWithAuthorsAndCommentsDto(book)))
                .sort(this::compareBooksByRating);
    }

    private boolean shouldIncludeBook(Book book, String userId, String sort) {
        if ("positive".equalsIgnoreCase(sort)) {
            return hasPositiveVoteByUser(book, userId);
        } else if ("negative".equalsIgnoreCase(sort)) {
            return hasNegativeVoteByUser(book, userId);
        }
        return false;
    }

    private boolean hasPositiveVoteByUser(Book book, String userId) {
        return book.getVoteByUserList().stream()
                .anyMatch(vote -> vote.getUserId().equals(userId) && vote.isPositive());
    }

    private boolean hasNegativeVoteByUser(Book book, String userId) {
        return book.getVoteByUserList().stream()
                .anyMatch(vote -> vote.getUserId().equals(userId) && !vote.isPositive());
    }

    private int compareBooksByRating(BookWithAuthorInfoAndCommentsDto bookDto1,
                                     BookWithAuthorInfoAndCommentsDto bookDto2) {
        return Double.compare(bookDto2.getRating(), bookDto1.getRating());
    }

    private Mono<BookWithAuthorInfoDto> enrichBookWithAuthorInfo(Book book) {
        return fetchAuthorInfo(book.getAuthorId())
                .map(authorDto -> {
                    var bookWithAuthorsDto = bookMapper.mapToBookWithAuthorsDto(book);
                    bookWithAuthorsDto.setAuthorName(authorDto.getName());
                    return bookWithAuthorsDto;
                })
                .doOnNext(bookDto -> log.info("Enriched book with author info: {}", bookDto));
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
                .doOnNext(dto -> log.info("Enriched book with author info and comments: {}", dto));
    }

    private Mono<AuthorResponseDto> fetchAuthorInfo(String authorId) {
        return webClientBuilder.build()
                .get()
                .uri("http://author-service/api/v1/authors/{id}", authorId)
                .retrieve()
                .bodyToMono(AuthorResponseDto.class)
                .doOnNext(authorDto -> log.info("Received author information: {}", authorDto))
                .transform(it -> cbFactory.create("author-service")
                        .run(it, throwable -> Mono.just(new AuthorResponseDto(null,
                                "Author information not available", null))));

    }

    private Flux<CommentResponseDto> fetchComments(String bookId) {
        return webClientBuilder.build()
                .get()
                .uri("http://comment-service/api/v1/comments/book/{id}", bookId)
                .retrieve()
                .bodyToFlux(CommentResponseDto.class)
                .transform(it -> cbFactory.create("comment-service")
                        .run(it, throwable -> Flux.just(new CommentResponseDto(null,
                                "Comments information not available", null, null,
                                null, null, null))))
                .doOnNext(commentDto -> log.info("Received comment: {}", commentDto));
    }

    private int compareBooks(BookWithAuthorInfoDto bookDto1, BookWithAuthorInfoDto bookDto2,
                             String order, boolean desc) {
        if ("createdAt".equalsIgnoreCase(order)) {
            return compareByCreatedAt(bookDto1, bookDto2, desc);
        } else {
            return compareByRating(bookDto1, bookDto2, desc);
        }
    }

    private int compareBooksByVote(BookWithAuthorInfoAndCommentsDto bookDto1,
                                   BookWithAuthorInfoAndCommentsDto bookDto2, String sort) {
        if ("positive".equalsIgnoreCase(sort)) {
            return Integer.compare(bookDto2.getPositiveVotesCount(), bookDto1.getPositiveVotesCount());
        } else if ("negative".equalsIgnoreCase(sort)) {
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