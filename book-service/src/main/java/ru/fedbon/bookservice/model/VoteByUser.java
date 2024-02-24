package ru.fedbon.bookservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoteByUser {

    @NotBlank
    private String id;

    @NotBlank
    private String userId;

    @NotBlank
    private boolean isPositive;
}
