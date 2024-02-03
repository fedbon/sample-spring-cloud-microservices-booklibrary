package ru.fedbon.voteservice.dto;

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
public class VoteDto {

    @NotBlank
    private String id;

    @NotBlank
    private String text;

    @NotBlank
    private String bookTitle;

    @NotBlank
    private String bookAuthorName;

    private String timeAgo;

    private int likeCount;

    private int dislikeCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VoteDto voteDto = (VoteDto) o;
        return Objects.equals(id, voteDto.id) &&
                Objects.equals(text, voteDto.text) &&
                Objects.equals(bookTitle, voteDto.bookTitle) &&
                Objects.equals(bookAuthorName, voteDto.bookAuthorName) &&
                Objects.equals(timeAgo, voteDto.timeAgo) &&
                likeCount == voteDto.likeCount &&
                dislikeCount == voteDto.dislikeCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, bookTitle, bookAuthorName,
                timeAgo, likeCount, dislikeCount);
    }
}
