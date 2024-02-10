package ru.fedbon.voteservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VoteAuthorRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String authorId;

    private Boolean isEnabled;
}
