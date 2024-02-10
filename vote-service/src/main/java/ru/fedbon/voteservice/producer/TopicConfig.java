package ru.fedbon.voteservice.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic bookTopic() {
        return TopicBuilder
                .name("bookTopic")
                .build();
    }

    @Bean
    public NewTopic authorTopic() {
        return TopicBuilder
                .name("authorTopic")
                .build();
    }

    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder
                .name("commentTopic")
                .build();
    }
}
