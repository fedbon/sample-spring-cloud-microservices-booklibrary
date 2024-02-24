package ru.fedbon.voteservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

import static ru.fedbon.voteservice.constants.AppConstants.AUTHOR_TOPIC;
import static ru.fedbon.voteservice.constants.Message.OFFSET_SENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveProducerService {

    private final ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate;

    public void sendVoteAuthorMessage(VoteAuthorMessage message) {
        reactiveKafkaProducerTemplate.send(AUTHOR_TOPIC, message)
                .doOnSuccess(senderResult -> log.info(OFFSET_SENT, message, senderResult.recordMetadata().offset()))
                .subscribe();
    }

}
