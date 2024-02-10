package ru.fedbon.voteservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveProducerService {

    private final ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate;

    public void sendVoteBookMessage(VoteBookMessage message) {
        reactiveKafkaProducerTemplate.send("bookTopic", message)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}",
                                        message, senderResult.recordMetadata().offset()))
                .subscribe();
    }

    public void sendVoteAuthorMessage(VoteAuthorMessage message) {
        reactiveKafkaProducerTemplate.send("authorTopic", message)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}",
                                message, senderResult.recordMetadata().offset()))
                .subscribe();
    }

    public void sendVoteCommentMessage(VoteCommentMessage message) {
        reactiveKafkaProducerTemplate.send("commentTopic", message)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}",
                                message, senderResult.recordMetadata().offset()))
                .subscribe();
    }

}
