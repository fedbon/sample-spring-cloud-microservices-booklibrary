package ru.fedbon.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String authorName;

    @NotBlank
    private String authorId;

    @NotBlank
    private String description;

    private List<CommentResponseDto> comments;

    private Integer positiveVotesCount;

    private Integer negativeVotesCount;

    private Integer commentsCount;

    @NotBlank
    private String genre;

    private String createdAt;

    private Double rating;
}

