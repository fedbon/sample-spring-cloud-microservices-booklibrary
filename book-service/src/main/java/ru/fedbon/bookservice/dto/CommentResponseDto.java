package ru.fedbon.bookservice.dto;

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

    private String id;

    private String text;

    private String userId;

    private String username;

    private String timeAgo;

    private Integer positiveVotesCount;

    private Integer negativeVotesCount;
}
