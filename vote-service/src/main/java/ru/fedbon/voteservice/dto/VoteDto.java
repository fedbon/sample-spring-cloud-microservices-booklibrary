package ru.fedbon.voteservice.dto;

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
public class VoteDto {

    @NotBlank
    private String id;

    @NotBlank
    private String userId;

    @NotBlank
    private String voteType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var voteDto = (VoteDto) o;
        return Objects.equals(id, voteDto.id) &&
                Objects.equals(userId, voteDto.userId) &&
                Objects.equals(voteType, voteDto.voteType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, voteType);
    }
}
