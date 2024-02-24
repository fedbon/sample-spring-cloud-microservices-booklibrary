package ru.fedbon.authorservice.config;


import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import ru.fedbon.authorservice.consumer.VoteAuthorMessage;

import java.util.Collections;

import static ru.fedbon.authorservice.constants.AppConstants.AUTHOR_TOPIC;

@Configuration
public class ReactiveKafkaConsumerConfig {

    @Bean
    public ReactiveKafkaConsumerTemplate<String, VoteAuthorMessage> reactiveKafkaConsumerTemplate(
            ReceiverOptions<String, VoteAuthorMessage> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReceiverOptions<String, VoteAuthorMessage> receiverOptions(
            KafkaProperties kafkaProperties, SslBundles sslBundles) {
        return ReceiverOptions.<String, VoteAuthorMessage>create(kafkaProperties
                        .getConsumer().buildProperties(sslBundles))
                .subscription(Collections.singleton(AUTHOR_TOPIC));
    }
}