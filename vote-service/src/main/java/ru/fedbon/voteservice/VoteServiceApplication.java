package ru.fedbon.voteservice;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@EnableMongock
@EnableReactiveMongoRepositories
@SpringBootApplication
@EnableDiscoveryClient
public class VoteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoteServiceApplication.class, args);
    }
}
