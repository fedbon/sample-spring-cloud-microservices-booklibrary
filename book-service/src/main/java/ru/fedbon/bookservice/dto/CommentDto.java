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
public class CommentDto {

    @NotBlank
    private String id;

    @NotBlank
    private String text;

    @NotBlank
    private String bookId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var commentDto = (CommentDto) o;
        return Objects.equals(id, commentDto.id) && Objects.equals(text, commentDto.text) &&
                Objects.equals(bookId, commentDto.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, bookId);
    }
}
