package ru.fedbon.voteservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fedbon.voteservice.dto.VoteBookRequest;
import ru.fedbon.voteservice.dto.VoteCommentRequest;
import ru.fedbon.voteservice.dto.VoteAuthorRequest;
import ru.fedbon.voteservice.exception.NotFoundException;
import ru.fedbon.voteservice.mapper.VoteMapper;
import ru.fedbon.voteservice.model.VoteAuthor;
import ru.fedbon.voteservice.model.VoteBook;
import ru.fedbon.voteservice.model.VoteComment;
import ru.fedbon.voteservice.producer.ReactiveProducerService;
import ru.fedbon.voteservice.producer.VoteAuthorMessage;
import ru.fedbon.voteservice.producer.VoteBookMessage;
import ru.fedbon.voteservice.producer.VoteCommentMessage;
import ru.fedbon.voteservice.repository.VoteAuthorRepository;
import ru.fedbon.voteservice.repository.VoteBookRepository;
import ru.fedbon.voteservice.repository.VoteCommentRepository;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/vote")
public class VoteRestControllerV1 {

    private final VoteBookRepository voteBookRepository;

    private final VoteCommentRepository voteCommentRepository;

    private final VoteAuthorRepository voteAuthorRepository;

    private final ReactiveProducerService reactiveProducerService;

    private final VoteMapper voteMapper;

//    @PostMapping(value = "/book/{id}")
//    public Mono<ResponseEntity<Void>> handleVoteBook(ServerHttpRequest http,
//                                                     @PathVariable("id") String bookId,
//                                                     @RequestBody VoteBookRequest request) {
//
//        var userId = http.getHeaders().getFirst("X-auth-user-id");
//
//        return voteBookRepository.findByUserId(userId)
//                .flatMap(existingVoteBook -> handleExistingVoteBook(existingVoteBook, userId, bookId, request))
//                .switchIfEmpty(Mono.error(new NotFoundException("VoteBook not found")))
//                .switchIfEmpty(sendNewVoteBookMessage(userId, bookId, request));
//    }

//    @PostMapping(value = "/comment/{id}")
//    public Mono<ResponseEntity<Void>> handleVoteComment(ServerHttpRequest http,
//                                                        @PathVariable String id,
//                                                        @RequestBody VoteCommentRequest request) {
//
//        var userId = http.getHeaders().getFirst("X-auth-user-id");
//
//        return voteCommentRepository.findByUserId(userId)
//                .flatMap(existingVoteComment -> handleExistingVoteComment(existingVoteComment, userId, id, request))
//                .switchIfEmpty(Mono.error(new NotFoundException("VoteComment not found")))
//                .switchIfEmpty(sendNewVoteCommentMessage(userId, id, request));
//    }

    @PostMapping(value = "/author")
    public Mono<ResponseEntity<Void>> handleVoteAuthor(ServerHttpRequest http,
                                                       @RequestBody VoteAuthorRequest request) {
        request.setUserId(http.getHeaders().getFirst("X-auth-user-id"));

        log.info("Received request to handle vote author for user ID: {}", request.getUserId());

        return voteAuthorRepository.findByUserId(request.getUserId())
                .flatMap(existingVoteAuthor -> handleExistingVoteAuthor(existingVoteAuthor, request))
                .switchIfEmpty(createAndSaveVoteAuthor(request));
    }

//    private Mono<ResponseEntity<Void>> handleExistingVoteBook(VoteBook existingVoteBook,
//                                                              String userId,
//                                                              String bookId,
//                                                              VoteBookRequest request) {
//        if (existingVoteBook != null) {
//            if (existingVoteBook.getIsPositive().equals(request.getIsPositive())) {
//                existingVoteBook.setIsPositive(null);
//            } else {
//                existingVoteBook.setIsPositive(request.getIsPositive());
//            }
//            return Mono.just(ResponseEntity.ok().build());
//        } else {
//            return sendNewVoteBookMessage(userId, bookId, request);
//        }
//    }
//
//    private Mono<ResponseEntity<Void>> handleExistingVoteComment(VoteComment existingVoteComment,
//                                                                 String userId,
//                                                                 String commentId,
//                                                                 VoteCommentRequest request) {
//        if (existingVoteComment != null) {
//            if (existingVoteComment.getIsPositive().equals(request.getIsPositive())) {
//                existingVoteComment.setIsPositive(null);
//            } else {
//                existingVoteComment.setIsPositive(request.getIsPositive());
//            }
//            return Mono.just(ResponseEntity.ok().build());
//        } else {
//            return sendNewVoteCommentMessage(userId, commentId, request);
//        }
//    }

    private Mono<ResponseEntity<Void>> handleExistingVoteAuthor(VoteAuthor existingVoteAuthor,
                                                                VoteAuthorRequest request) {
        if (existingVoteAuthor != null) {
            if (existingVoteAuthor.getIsEnabled() == null ||
                    !existingVoteAuthor.getIsEnabled().equals(request.getIsEnabled())) {
                existingVoteAuthor.setIsEnabled(request.getIsEnabled());
                // Log message indicating a change in isEnabled
                log.info("Setting isEnabled for existing vote author (ID: {}) to: {}",
                        existingVoteAuthor.getId(), request.getIsEnabled());
                // Send message only when there's a change in isEnabled
                sendVoteAuthorMessage(request);
            }
            // Log information about saving the existing vote author
            log.info("Saving existing vote author: {}", existingVoteAuthor);
            // Save the existingVoteAuthor with possible changes
            sendVoteAuthorMessage(request);

            return voteAuthorRepository.save(existingVoteAuthor)
                    .thenReturn(ResponseEntity.ok().build());
        } else {
            // Log information about creating and saving a new vote author
            log.info("No existing vote author found. Creating and saving new vote author for request: {}", request);
            // Create and save new VoteAuthor if it doesn't exist
            return createAndSaveVoteAuthor(request);
        }
    }


//    private Mono<ResponseEntity<Void>> sendNewVoteBookMessage(String userId,
//                                                              String bookId,
//                                                              VoteBookRequest request) {
//        var voteBookMessage = new VoteBookMessage(
//                userId,
//                bookId,
//                request.getIsPositive());
//        reactiveProducerService.sendVoteBookMessage(voteBookMessage);
//        return Mono.just(ResponseEntity.ok().build());
//    }
//
//    private Mono<ResponseEntity<Void>> sendNewVoteCommentMessage(String userId,
//                                                                 String commentId,
//                                                                 VoteCommentRequest request) {
//        var voteCommentMessage = new VoteCommentMessage(
//                userId,
//                commentId,
//                request.getIsPositive());
//        reactiveProducerService.sendVoteCommentMessage(voteCommentMessage);
//        return Mono.just(ResponseEntity.ok().build());
//    }

    private void sendVoteAuthorMessage(VoteAuthorRequest request) {
        var voteAuthorMessage = new VoteAuthorMessage(
                request.getUserId(),
                request.getAuthorId(),
                request.getIsEnabled());

        // Log information about the vote author message being sent
        log.info("Sending vote author message: {}", voteAuthorMessage);

        reactiveProducerService.sendVoteAuthorMessage(voteAuthorMessage);
    }

    private Mono<ResponseEntity<Void>> createAndSaveVoteAuthor(VoteAuthorRequest request) {
        return voteAuthorRepository.save(voteMapper.mapToVoteAuthor(request))
                .flatMap(savedVoteAuthor -> {
                    // Log information about the creation and saving of a vote author
                    log.info("Vote author created and saved: {}", savedVoteAuthor);

                    // Send vote author message
                    sendVoteAuthorMessage(request);

                    return Mono.just(ResponseEntity.ok().build());
                });
    }
}