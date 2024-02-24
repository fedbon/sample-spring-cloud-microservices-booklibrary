package ru.fedbon.commentservice.model;

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
    private String userId;

    @NotBlank
    private boolean isPositive;
}
