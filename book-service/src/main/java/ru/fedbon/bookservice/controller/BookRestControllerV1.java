package ru.fedbon.bookservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.bookservice.dto.AuthorResponseDto;
import ru.fedbon.bookservice.dto.BookDto;
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

    @GetMapping
    public Flux<BookDto> handleGetAll(
            @RequestParam(name = "order", defaultValue = "relevancy") String order,
            @RequestParam(name = "desc", defaultValue = "true") boolean desc) {

        var sort = getSort(order, desc);

        return bookRepository.findAll(sort)
                .flatMap(this::enrichBookWithAuthorInfo);
    }

    @GetMapping(params = "genre")
    public Flux<BookDto> handleGetAllByGenreId(
            @RequestParam(name = "genre") String genreId,
            @RequestParam(name = "order", defaultValue = "relevancy") String order,
            @RequestParam(name = "desc", defaultValue = "true") boolean desc) {

        var sort = getSort(order, desc);

        return bookRepository.findAllByGenreId(genreId, sort)
                .flatMap(this::enrichBookWithAuthorInfo);
    }

    @GetMapping(value = "/{id}")
    public Mono<BookDto> handleGetById(@PathVariable(value = "id") String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Book not found")))
                .flatMap(book -> enrichBookWithAuthorInfoAndComments(bookMapper.map(book)));
    }

    @GetMapping(params = "author")
    public Flux<BookDto> handleGetAllByAuthorId(@RequestParam(value = "author") String authorId,
                                                @RequestParam(name = "order", defaultValue = "rating") String order,
                                                @RequestParam(name = "desc", defaultValue = "true") boolean desc) {

        var sort = getSort(order, desc);

        return bookRepository.findAllByAuthorId(authorId, sort)
                .flatMap(this::enrichBookWithAuthorInfo)
                .doOnNext(bookDto -> log.info("Books retrieved successfully for Author ID: {}", authorId))
                .doOnError(error -> log.error("Error occurred while retrieving books by Author ID {}: {}",
                        authorId, error.getMessage(), error));
    }

    private Mono<BookDto> enrichBookWithAuthorInfo(Book book) {
        return fetchAuthorInfo(book.getAuthorId())
                .map(authorDto -> {
                    var bookDto = bookMapper.map(book);
                    bookDto.setAuthorName(authorDto.getName());
                    return bookDto;
                });
    }

    private Mono<BookDto> enrichBookWithAuthorInfoAndComments(BookDto bookDto) {
        return fetchAuthorInfo(bookDto.getAuthorName())
                .flatMap(authorDto -> {
                    var commentsFlux = fetchComments(bookDto.getId());
                    return commentsFlux.collectList()
                            .map(comments -> {
                                bookDto.setAuthorName(authorDto.getName());
                                bookDto.setComments(comments);
                                return bookDto;
                            });
                });
    }

    private Mono<AuthorResponseDto> fetchAuthorInfo(String authorId) {
        return webClientBuilder.build()
                .get()
                .uri("http://author-service/api/v1/author/{id}", authorId)
                .retrieve()
                .bodyToMono(AuthorResponseDto.class)
                .transform(it -> cbFactory.create("author-service")
                        .run(it, throwable -> Mono.just(new AuthorResponseDto(null,
                                "Author information not available", null))))
                .doOnNext(authorDto -> log.info("Received author information: {}", authorDto));
    }

    private Flux<CommentResponseDto> fetchComments(String bookId) {
        return webClientBuilder.build()
                .get()
                .uri("http://comment-service/api/v1/comments/book/{id}", bookId)
                .retrieve()
                .bodyToFlux(CommentResponseDto.class)
                .transform(it -> cbFactory.create("comment-service")
                        .run(it, throwable -> Flux.just(new CommentResponseDto(null,
                                "Comments not available", null, null,
                                null, null, null))))
                .doOnNext(commentDto -> log.info("Received comment: {}", commentDto));
    }

    private Sort getSort(String order, boolean desc) {
        if ("relevancy".equalsIgnoreCase(order)) {
            return desc ? Sort.by(Sort.Direction.DESC, "rating")
                    : Sort.by(Sort.Direction.ASC, "rating");
        } else {
            return desc ? Sort.by(Sort.Direction.DESC, "createdAt")
                    : Sort.by(Sort.Direction.ASC, "createdAt");
        }
    }
}


