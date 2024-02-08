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
public class BookWithAuthorInfoDto {

    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String authorName;

    @NotBlank
    private String description;

    private String createdAt;

    private Double rating;
}

