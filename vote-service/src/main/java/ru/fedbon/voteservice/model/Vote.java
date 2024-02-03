package ru.fedbon.voteservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("votes")
public class Vote {

    @Id
    private String id;

    private String text;

    private String bookId;

    private String bookTitle;

    private String bookAuthorName;

    private String userId;

    private String username;

    private LocalDateTime createdAt;

    private int likeCount;

    private int dislikeCount;
}
