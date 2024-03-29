package ru.fedbon.authorservice.consumer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.fedbon.authorservice.model.Author;
import ru.fedbon.authorservice.model.VoteByUser;

import ru.fedbon.authorservice.repository.AuthorRepository;

import static ru.fedbon.authorservice.constants.ErrorMessage.CONSUMPTION_PROBLEM_MESSAGE;
import static ru.fedbon.authorservice.constants.Message.CONSUMER_RECORD;
import static ru.fedbon.authorservice.constants.Message.SUCCESSFULLY_CONSUMED_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveConsumer {

    private final ReactiveKafkaConsumerTemplate<String, VoteAuthorMessage> reactiveKafkaConsumerTemplate;

    private final AuthorRepository authorRepository;

    @PostConstruct
    public void init() {
        consumeVoteAuthorMessage().subscribe();
    }

    public Flux<VoteAuthorMessage> consumeVoteAuthorMessage() {
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                .flatMap(consumerRecord -> {
                    log.info(CONSUMER_RECORD,
                            consumerRecord.key(),
                            consumerRecord.value(),
                            consumerRecord.topic(),
                            consumerRecord.offset());

                    var voteAuthorMessage = consumerRecord.value();
                    return authorRepository.findById(voteAuthorMessage.getAuthorId())
                            .flatMap(author -> {
                                updateOrAddVote(author, voteAuthorMessage);
                                return authorRepository.save(author).thenReturn(voteAuthorMessage);
                            })
                            .doOnNext(msg -> log.info(SUCCESSFULLY_CONSUMED_MESSAGE,
                                    VoteAuthorMessage.class.getSimpleName(), msg))
                            .doOnError(throwable -> log.error(CONSUMPTION_PROBLEM_MESSAGE,
                                    throwable.getMessage()));
                });
    }

    private void updateOrAddVote(Author author, VoteAuthorMessage message) {
        var existingVote = author.getVoteByUserList().stream()
                .filter(vote -> vote.getUserId().equals(message.getUserId()))
                .findFirst()
                .orElse(null);
        if (existingVote != null) {
            existingVote.setIsEnabled(message.isEnabled());
        } else {
            var newVote = new VoteByUser();
            newVote.setUserId(message.getUserId());
            newVote.setIsEnabled(message.isEnabled());
            author.getVoteByUserList().add(newVote);
        }
    }
}

