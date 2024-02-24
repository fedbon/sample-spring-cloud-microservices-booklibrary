package ru.fedbon.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BookWithAuthorInfoAndCommentsDto {

    private String id;

    private String title;

    private String authorName;

    private String authorId;

    private String description;

    private List<CommentResponseDto> comments;

    private Integer positiveVotesCount;

    private Integer negativeVotesCount;

    private Integer commentsCount;

    private String genre;

    private String createdAt;

    private Double rating;
}

