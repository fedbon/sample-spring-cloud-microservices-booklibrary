package ru.fedbon.userserver.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountDto {

    @NotBlank
    private String username;

    @NotBlank
    private Integer booksCount;

    @NotBlank
    private Integer authorsCount;

}
