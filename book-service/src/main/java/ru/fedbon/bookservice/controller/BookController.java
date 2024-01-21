package ru.fedbon.bookservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.bookservice.dto.BookCreateDto;
import ru.fedbon.bookservice.dto.BookDto;
import ru.fedbon.bookservice.dto.BookUpdateDto;
import ru.fedbon.bookservice.exception.NotFoundException;
import ru.fedbon.bookservice.mapper.BookMapper;
import ru.fedbon.bookservice.repository.AuthorRepository;
import ru.fedbon.bookservice.repository.BookRepository;
import ru.fedbon.bookservice.repository.GenreRepository;
import ru.fedbon.bookservice.util.ErrorMessage;

import static java.lang.String.format;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    private final AuthorRepository authorRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> handleCreate(@Valid @RequestBody BookCreateDto bookCreateDto) {
        return genreRepository.findById(bookCreateDto.getGenreId())
                .switchIfEmpty(Mono.error(new NotFoundException(
                        format(ErrorMessage.GENRE_NOT_FOUND, bookCreateDto.getGenreId())
                )))
                .zipWith(authorRepository.findById(bookCreateDto.getAuthorId())
                        .switchIfEmpty(Mono.error(new NotFoundException(
                                format(ErrorMessage.AUTHOR_NOT_FOUND, bookCreateDto.getAuthorId())
                        ))))
                .flatMap(tuple -> {
                    var genre = tuple.getT1();
                    var author = tuple.getT2();
                    var newBook = BookMapper.mapDtoToNewBook(bookCreateDto, genre, author);
                    return bookRepository.save(newBook)
                            .map(BookMapper::mapBookToDto);
                });
    }

    @PutMapping(value = "/{id}")
    public Mono<BookDto> handleUpdate(@PathVariable(value = "id") String id,
                                      @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        return genreRepository.findById(bookUpdateDto.getGenreId())
                .switchIfEmpty(Mono.error(new NotFoundException(
                        format(ErrorMessage.GENRE_NOT_FOUND, bookUpdateDto.getGenreId())
                )))
                .zipWith(authorRepository.findById(bookUpdateDto.getAuthorId())
                        .switchIfEmpty(Mono.error(new NotFoundException(
                                format(ErrorMessage.AUTHOR_NOT_FOUND, bookUpdateDto.getAuthorId())
                        ))))
                .flatMap(tuple -> bookRepository.findById(bookUpdateDto.getId())
                        .switchIfEmpty(Mono.error(new NotFoundException(
                                format(ErrorMessage.BOOK_NOT_FOUND, bookUpdateDto.getId())
                        )))
                        .flatMap(book -> {
                            var genre = tuple.getT1();
                            var author = tuple.getT2();
                            book.setTitle(bookUpdateDto.getTitle());
                            book.setGenre(genre);
                            book.setAuthor(author);
                            return bookRepository.save(book)
                                    .map(BookMapper::mapBookToDto);
                        }));
    }

    @GetMapping
    public Flux<BookDto> handleGetAll(ServerHttpRequest request) {
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .map(BookMapper::mapBookToDto);
    }

    @GetMapping(value = "/{id}")
    public Mono<BookDto> handleGetById(@PathVariable(value = "id") String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(format(ErrorMessage.BOOK_NOT_FOUND, id))))
                .map(BookMapper::mapBookToDto);
    }

    @GetMapping(params = "authorId")
    public Flux<BookDto> handleGetAllByAuthorId(@RequestParam(value = "authorId") String authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        format(ErrorMessage.AUTHOR_NOT_FOUND, authorId))))
                .flatMapMany(author -> bookRepository.findAllByAuthorId(author.getId())
                        .map(BookMapper::mapBookToDto));
    }

    @GetMapping(params = "genreId")
    public Flux<BookDto> handleGetAllByGenreId(@RequestParam(value = "genreId") String genreId) {
        return genreRepository.findById(genreId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        format(ErrorMessage.GENRE_NOT_FOUND, genreId))))
                .flatMapMany(genre -> bookRepository.findAllByGenreId(genre.getId())
                        .map(BookMapper::mapBookToDto));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> handleDelete(@PathVariable(value = "id") String id) {
        return bookRepository.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> handleDeleteAll() {
        return bookRepository.deleteAll();
    }
}



