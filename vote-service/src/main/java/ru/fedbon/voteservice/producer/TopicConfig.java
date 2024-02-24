package ru.fedbon.voteservice.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static ru.fedbon.voteservice.constants.AppConstants.AUTHOR_TOPIC;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic authorTopic() {
        return TopicBuilder
                .name(AUTHOR_TOPIC)
                .build();
    }

}
