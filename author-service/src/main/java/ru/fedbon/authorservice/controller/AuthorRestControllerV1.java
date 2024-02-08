package ru.fedbon.authorservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.authorservice.dto.AuthorDto;
import ru.fedbon.authorservice.exception.NotFoundException;
import ru.fedbon.authorservice.mapper.AuthorMapper;
import ru.fedbon.authorservice.repository.AuthorRepository;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/authors")
public class AuthorRestControllerV1 {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @GetMapping("/user/{id}/count")
    public Mono<Long> countUsersVotedForAuthor(@PathVariable String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("No author found with id: " + id)))
                .map(author -> (long) author.getVoteByUserList().size())
                .doOnSuccess(count -> log.info("Counted users voted for author with id {}: {}", id, count));
    }

    @GetMapping("/{id}")
    public Mono<AuthorDto> handleGetById(@PathVariable String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("No authors found voted by user with id: " + id)))
                .map(authorMapper::map)
                .doOnSuccess(authorDto -> log.info("Successfully retrieved author with id: {}", id));
    }

    @GetMapping("/user/{id}")
    public Flux<AuthorDto> handleGetAuthorsVotedByUserId(@PathVariable String id) {
        return authorRepository.findAll()
                .filter(author -> author.getVoteByUserList().stream().anyMatch(vote -> vote.getUserId().equals(id)))
                .switchIfEmpty(Mono.error(new NotFoundException("No authors found voted by user with id: " + id)))
                .map(authorMapper::map)
                .doOnNext(authorDto -> log.info("Author voted by user with id {} found: {}", id, authorDto.getName()));
    }
}
