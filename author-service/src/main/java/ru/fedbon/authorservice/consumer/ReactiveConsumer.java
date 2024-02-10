package ru.fedbon.authorservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.authorservice.model.Author;
import ru.fedbon.authorservice.model.VoteByUser;

import ru.fedbon.authorservice.repository.AuthorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveConsumer implements CommandLineRunner {

    private final ReactiveKafkaConsumerTemplate<String, VoteAuthorMessage> reactiveKafkaConsumerTemplate;

    private final AuthorRepository authorRepository;

    public Flux<VoteAuthorMessage> consumeVoteAuthorMessage() {
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                .flatMap(this::processConsumerRecord);
    }

    private Mono<VoteAuthorMessage> processConsumerRecord(ConsumerRecord<String, VoteAuthorMessage> consumerRecord) {
        log.info("Received key={}, value={} from topic={}, offset={}",
                consumerRecord.key(),
                consumerRecord.value(),
                consumerRecord.topic(),
                consumerRecord.offset());

        VoteAuthorMessage voteAuthorMessage = consumerRecord.value();
        return authorRepository.findById(voteAuthorMessage.getAuthorId())
                .flatMap(author -> {
                    updateOrAddVote(author, voteAuthorMessage);
                    return authorRepository.save(author).thenReturn(voteAuthorMessage);
                })
                .doOnNext(msg -> log.info("Successfully consumed {}={}", VoteAuthorMessage.class.getSimpleName(), msg))
                .doOnError(throwable -> log.error("Something bad happened while processing message: {}",
                        throwable.getMessage()));
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

    @Override
    public void run(String... args) {
        consumeVoteAuthorMessage().subscribe();
    }
}

