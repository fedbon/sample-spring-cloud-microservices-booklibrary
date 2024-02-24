package ru.fedbon.authorservice.consumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static ru.fedbon.authorservice.constants.AppConstants.AUTHOR_TOPIC;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder
                .name(AUTHOR_TOPIC)
                .build();
    }
}
