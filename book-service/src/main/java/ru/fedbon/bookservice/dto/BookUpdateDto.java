package ru.fedbon.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookUpdateDto {

    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String genreId;

    @NotBlank
    private String authorId;
}
