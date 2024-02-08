package ru.fedbon.bookservice.dto;

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
public class CommentResponseDto {

    @NotBlank
    private String id;

    @NotBlank
    private String text;

    private String userId;

    private String username;

    private String timeAgo;

    private Integer positiveVotesCount;

    private Integer negativeVotesCount;
}