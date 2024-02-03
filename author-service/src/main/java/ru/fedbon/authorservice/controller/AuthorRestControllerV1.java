package ru.fedbon.authorservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fedbon.authorservice.dto.AuthorDto;
import ru.fedbon.authorservice.exception.NotFoundException;
import ru.fedbon.authorservice.mapper.AuthorMapper;
import ru.fedbon.authorservice.repository.AuthorRepository;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/author")
public class AuthorRestControllerV1 {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @GetMapping("/{id}")
    public Mono<AuthorDto> handleGetById(@PathVariable String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Author not found")))
                .map(authorMapper::map)
                .doOnSuccess(authorDto -> log.info("Author retrieved successfully. ID: {}", id))
                .doOnError(error -> log.error("Error occurred while retrieving author by ID {}: {}",
                        id, error.getMessage(), error));
    }
}
