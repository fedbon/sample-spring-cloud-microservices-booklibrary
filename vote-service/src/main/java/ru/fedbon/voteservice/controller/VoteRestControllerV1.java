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
import ru.fedbon.voteservice.mapper.VoteMapper;
import ru.fedbon.voteservice.model.VoteAuthor;
import ru.fedbon.voteservice.producer.ReactiveProducerService;
import ru.fedbon.voteservice.producer.VoteAuthorMessage;
import ru.fedbon.voteservice.repository.VoteAuthorRepository;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/vote")
public class VoteRestControllerV1 {

    private final VoteAuthorRepository voteAuthorRepository;

    private final ReactiveProducerService reactiveProducerService;

    private final VoteMapper voteMapper;

    @PostMapping(value = "/author")
    public Mono<ResponseEntity<Void>> handleVoteAuthor(ServerHttpRequest http,
                                                       @RequestBody VoteAuthorRequest request) {

        request.setUserId(http.getHeaders().getFirst("X-auth-user-id"));

        log.info("Received request to handle vote author for user ID: {}", request.getUserId());

        return voteAuthorRepository.findByUserId(request.getUserId())
                .flatMap(existingVoteAuthor -> handleExistingVoteAuthor(existingVoteAuthor, request))
                .switchIfEmpty(createAndSaveVoteAuthor(request));
    }

    private Mono<ResponseEntity<Void>> handleExistingVoteAuthor(VoteAuthor existingVoteAuthor,
                                                                VoteAuthorRequest request) {
        if (existingVoteAuthor != null) {
            if (existingVoteAuthor.getIsEnabled() == null ||
                    !existingVoteAuthor.getIsEnabled().equals(request.getIsEnabled())) {
                existingVoteAuthor.setIsEnabled(request.getIsEnabled());
                log.info("Setting isEnabled for existing vote author (ID: {}) to: {}",
                        existingVoteAuthor.getId(), request.getIsEnabled());
                sendVoteAuthorMessage(request);
            }
            log.info("Saving existing vote author: {}", existingVoteAuthor);
            sendVoteAuthorMessage(request);

            return voteAuthorRepository.save(existingVoteAuthor)
                    .thenReturn(ResponseEntity.ok().build());
        } else {
            log.info("No existing vote author found. Creating and saving new vote author for request: {}", request);
            return createAndSaveVoteAuthor(request);
        }
    }

    private void sendVoteAuthorMessage(VoteAuthorRequest request) {
        var voteAuthorMessage = new VoteAuthorMessage(
                request.getUserId(),
                request.getAuthorId(),
                request.getIsEnabled());
        log.info("Sending vote author message: {}", voteAuthorMessage);
        reactiveProducerService.sendVoteAuthorMessage(voteAuthorMessage);
    }

    private Mono<ResponseEntity<Void>> createAndSaveVoteAuthor(VoteAuthorRequest request) {
        return voteAuthorRepository.save(voteMapper.mapToVoteAuthor(request))
                .flatMap(savedVoteAuthor -> {
                    log.info("Vote author created and saved: {}", savedVoteAuthor);
                    sendVoteAuthorMessage(request);
                    return Mono.just(ResponseEntity.ok().build());
                });
    }
}