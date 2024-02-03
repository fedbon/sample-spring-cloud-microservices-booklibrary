package ru.fedbon.voteservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.fedbon.voteservice.exception.NotFoundException;
import ru.fedbon.voteservice.model.Vote;
import ru.fedbon.voteservice.repository.VoteRepository;
import ru.fedbon.voteservice.dto.VoteDto;
import ru.fedbon.voteservice.mapper.VoteMapper;


import java.util.Comparator;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/vote")
public class VoteRestControllerV1 {

    private final VoteRepository voteRepository;

    private final VoteMapper voteMapper;

    @GetMapping(value = "/book/{bookId}")
    public Flux<VoteDto> handleGetAllByBookId(@PathVariable(value = "bookId") String bookId) {
        try {
            return voteRepository.findAllByBookId(Sort.by(Sort.Direction.DESC, "createdAt"), bookId)
                    .map(voteMapper::map)
                    .doOnComplete(() -> log.info("All reviews retrieved successfully for bookTitle: {}", bookId));
        } catch (Exception e) {
            log.error("Error occurred while retrieving reviews for bookTitle: {}", e.getMessage(), e);
            throw new NotFoundException("Failed to retrieve reviews by bookTitle");
        }
    }

    @GetMapping(value = "/user/{userId}")
    public Flux<VoteDto> handleGetAllByUserId(@PathVariable(value = "userId") String userId) {
        try {
            Flux<Vote> userReviews = voteRepository.findAllByUserId(Sort.by(Sort.Direction.DESC,
                    "createdAt"), userId);

            return userReviews
                    .collectMultimap(Vote::getBookId)
                    .flatMapMany(reviewsByBookId -> Flux.fromIterable(reviewsByBookId.entrySet())
                            .flatMap(entry -> Flux.fromIterable(entry.getValue())
                                    .sort(Comparator.comparing(Vote::getCreatedAt).reversed())
                                    .next()
                                    .map(voteMapper::map)
                            )
                    )
                    .doOnComplete(() -> log.info("All reviews retrieved successfully for username: {}", userId));
        } catch (Exception e) {
            log.error("Error occurred while retrieving reviews by username: {}", e.getMessage(), e);
            throw new NotFoundException("Failed to retrieve reviews by username");
        }
    }
}
