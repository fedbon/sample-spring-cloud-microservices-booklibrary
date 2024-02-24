package ru.fedbon.voteservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fedbon.voteservice.dto.VoteAuthorRequest;
import ru.fedbon.voteservice.exception.NotFoundException;
import ru.fedbon.voteservice.mapper.VoteMapper;
import ru.fedbon.voteservice.model.VoteAuthor;
import ru.fedbon.voteservice.producer.ReactiveProducerService;
import ru.fedbon.voteservice.producer.VoteAuthorMessage;
import ru.fedbon.voteservice.repository.VoteAuthorRepository;

import static ru.fedbon.voteservice.constants.ErrorMessage.VOTE_AUTHOR_NOT_FOUND_BY_USER_ID;
import static ru.fedbon.voteservice.constants.Message.RECEIVED_REQUEST_TO_VOTE_AUTHOR_BY_USER_ID;
import static ru.fedbon.voteservice.constants.Message.SET_IS_ENABLED_FOR_EXISTING_AUTHOR_VOTE;
import static ru.fedbon.voteservice.constants.Message.VOTE_CREATED;
import static ru.fedbon.voteservice.constants.Message.VOTE_SENT;
import static ru.fedbon.voteservice.constants.Message.VOTE_UPDATED;
import static ru.fedbon.voteservice.constants.PathConstants.API_V1_VOTES;
import static ru.fedbon.voteservice.constants.PathConstants.AUTH_USER_ID_HEADER;
import static ru.fedbon.voteservice.constants.PathConstants.VOTE_AUTHOR;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_V1_VOTES)
public class VoteRestController {

    private final VoteAuthorRepository voteAuthorRepository;

    private final ReactiveProducerService reactiveProducerService;

    private final VoteMapper voteMapper;

    @PostMapping(value = VOTE_AUTHOR)
    public Mono<ResponseEntity<Void>> handleVoteAuthor(ServerHttpRequest http,
                                                       @RequestBody VoteAuthorRequest request) {
        request.setUserId(http.getHeaders().getFirst(AUTH_USER_ID_HEADER));
        log.info(RECEIVED_REQUEST_TO_VOTE_AUTHOR_BY_USER_ID, request.getUserId());

        return voteAuthorRepository.findByUserId(request.getUserId()).flatMap(existingVoteAuthor ->
                handleExistingVoteAuthor(existingVoteAuthor, request))
                .switchIfEmpty(createAndSaveVoteAuthor(request))
                .onErrorMap(NotFoundException.class, ex ->
                        new NotFoundException(VOTE_AUTHOR_NOT_FOUND_BY_USER_ID + request.getUserId()));
    }

    private Mono<ResponseEntity<Void>> handleExistingVoteAuthor(VoteAuthor existingVoteAuthor,
                                                                VoteAuthorRequest request) {
        if (existingVoteAuthor != null) {
            if (existingVoteAuthor.getIsEnabled() == null ||
                    !existingVoteAuthor.getIsEnabled().equals(request.getIsEnabled())) {
                existingVoteAuthor.setIsEnabled(request.getIsEnabled());
                log.info(SET_IS_ENABLED_FOR_EXISTING_AUTHOR_VOTE, existingVoteAuthor.getId(), request.getIsEnabled());
                sendVoteAuthorMessage(request);
            }
            log.info(VOTE_UPDATED, existingVoteAuthor);
            sendVoteAuthorMessage(request);
            return voteAuthorRepository.save(existingVoteAuthor).thenReturn(ResponseEntity.ok().build());
        } else {
            log.info(VOTE_CREATED, request);
            return createAndSaveVoteAuthor(request);
        }
    }

    private void sendVoteAuthorMessage(VoteAuthorRequest request) {
        var voteAuthorMessage = new VoteAuthorMessage(request.getUserId(), request.getAuthorId(),
                request.getIsEnabled());
        log.info(VOTE_SENT, voteAuthorMessage);
        reactiveProducerService.sendVoteAuthorMessage(voteAuthorMessage);
    }

    private Mono<ResponseEntity<Void>> createAndSaveVoteAuthor(VoteAuthorRequest request) {
        return voteAuthorRepository.save(voteMapper.mapToVoteAuthor(request)).flatMap(savedVoteAuthor -> {
            sendVoteAuthorMessage(request);
            return Mono.just(ResponseEntity.ok().build());
        });
    }
}