package ru.fedbon.userserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewResponseDto {

    @NotBlank
    private String id;

    @NotBlank
    private String text;

    private Mono<BookResponseDto> review;

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
        ReviewResponseDto reviewDto = (ReviewResponseDto) o;
        return Objects.equals(id, reviewDto.id) &&
                Objects.equals(text, reviewDto.text) &&
                Objects.equals(getBookResponseDto(), reviewDto.getBookResponseDto()) &&
                Objects.equals(timeAgo, reviewDto.timeAgo) &&
                likeCount == reviewDto.likeCount &&
                dislikeCount == reviewDto.dislikeCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, getBookResponseDto(), timeAgo, likeCount, dislikeCount);
    }

    private BookResponseDto getBookResponseDto() {
        return review != null ? review.block() : null;
    }
}
