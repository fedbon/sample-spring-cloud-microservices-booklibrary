package ru.fedbon.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseDto {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    private Integer positiveVotesCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var authorDto = (AuthorResponseDto) o;
        return Objects.equals(id, authorDto.id) &&
                Objects.equals(name, authorDto.name) &&
                Objects.equals(positiveVotesCount, authorDto.positiveVotesCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, positiveVotesCount);
    }
}
