package ru.fedbon.voteservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("votes_authors")
public class VoteAuthor {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String authorId;

    private Boolean isEnabled;
}
