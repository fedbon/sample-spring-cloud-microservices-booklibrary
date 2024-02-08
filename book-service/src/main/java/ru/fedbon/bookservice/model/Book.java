package ru.fedbon.bookservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("books")
public class Book {

    @Id
    private String id;

    private String title;

    @DBRef
    private Genre genre;

    private String authorId;

    private String description;

    private LocalDateTime createdAt;

    private List<VoteByUser> voteByUserList;
}
