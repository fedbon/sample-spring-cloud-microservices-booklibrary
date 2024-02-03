package ru.fedbon.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookDto {

    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String authorName;

    private List<CommentResponseDto> comments;

    @NotBlank
    private String genre;

    private String createdAt;

    private Double rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var bookDto = (BookDto) o;
        return Objects.equals(id, bookDto.id) &&
                Objects.equals(title, bookDto.title) &&
                Objects.equals(authorName, bookDto.authorName) &&
                Objects.equals(comments, bookDto.comments) &&
                Objects.equals(genre, bookDto.genre) &&
                Objects.equals(createdAt, bookDto.createdAt) &&
                Objects.equals(rating, bookDto.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, authorName, comments, genre, createdAt, rating);
    }
}

