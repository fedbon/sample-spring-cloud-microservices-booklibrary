package ru.fedbon.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var commentDto = (CommentResponseDto) o;
        return Objects.equals(id, commentDto.id) &&
                Objects.equals(text, commentDto.text) &&
                Objects.equals(userId, commentDto.userId) &&
                Objects.equals(username, commentDto.username) &&
                Objects.equals(timeAgo, commentDto.timeAgo) &&
                Objects.equals(positiveVotesCount, commentDto.positiveVotesCount) &&
                Objects.equals(negativeVotesCount, commentDto.negativeVotesCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, userId, username, timeAgo, positiveVotesCount, negativeVotesCount);
    }
}
