package ru.fedbon.commentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CommentDto {

    @NotBlank
    private String id;

    @NotBlank
    private String text;

    @NotBlank
    private String userId;

    @NotBlank
    private String username;

    @NotBlank
    private String bookId;

    private String timeAgo;

    private Integer positiveVotesCount;

    private Integer negativeVotesCount;
}
