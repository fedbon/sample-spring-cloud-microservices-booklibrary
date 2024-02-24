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

import static ru.fedbon.authorservice.constants.ErrorMessage.AUTHORS_NOT_FOUND;
import static ru.fedbon.authorservice.constants.ErrorMessage.AUTHOR_NOT_FOUND;
import static ru.fedbon.authorservice.constants.Message.AUTHOR_VOTED_BY_USER_ID_FOUND_MESSAGE;
import static ru.fedbon.authorservice.constants.Message.COUNT_VOTES_BY_AUTHOR_ID_MESSAGE;
import static ru.fedbon.authorservice.constants.Message.SUCCESSFULLY_RETRIEVED_AUTHOR_BY_ID_MESSAGE;
import static ru.fedbon.authorservice.constants.PathConstants.API_V1_AUTHORS;
import static ru.fedbon.authorservice.constants.PathConstants.AUTHORS_VOTED_BY_USER_ID;
import static ru.fedbon.authorservice.constants.PathConstants.AUTHOR_ID;
import static ru.fedbon.authorservice.constants.PathConstants.COUNT_VOTES_BY_AUTHOR_ID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_V1_AUTHORS)
public class AuthorRestController {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @GetMapping(COUNT_VOTES_BY_AUTHOR_ID)
    public Mono<Long> handleCountVotesByAuthorId(@PathVariable String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(AUTHOR_NOT_FOUND + id)))
                .map(author -> author.getVoteByUserList().stream()
                        .filter(vote -> Boolean.TRUE.equals(vote.getIsEnabled()))
                        .count())
                .doOnSuccess(count -> log.info(COUNT_VOTES_BY_AUTHOR_ID_MESSAGE, id, count));
    }

    @GetMapping(AUTHOR_ID)
    public Mono<AuthorDto> handleGetById(@PathVariable String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(AUTHOR_NOT_FOUND + id)))
                .map(authorMapper::map)
                .doOnSuccess(authorDto -> log.info(SUCCESSFULLY_RETRIEVED_AUTHOR_BY_ID_MESSAGE, id));
    }

    @GetMapping(AUTHORS_VOTED_BY_USER_ID)
    public Flux<AuthorDto> handleGetAuthorsVotedByUserId(@PathVariable String id) {
        return authorRepository.findAll()
                .filter(author -> author.getVoteByUserList().stream()
                        .anyMatch(vote -> vote.getUserId().equals(id) && Boolean.TRUE.equals(vote.getIsEnabled())))
                .switchIfEmpty(Mono.error(new NotFoundException(AUTHORS_NOT_FOUND + id)))
                .map(authorMapper::map)
                .doOnNext(authorDto -> log.info(AUTHOR_VOTED_BY_USER_ID_FOUND_MESSAGE, id, authorDto.getName()));
    }
}
