package ru.fedbon.commentservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("comments")
public class Comment {

    @Id
    private String id;

    private String text;

    private String bookId;

    private String userId;

    private String username;

    private LocalDateTime createdAt;

    private List<VoteByUser> voteByUserList;
}
