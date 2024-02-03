package ru.fedbon.userserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookResponseDto {

    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String bookAuthorName;
}
