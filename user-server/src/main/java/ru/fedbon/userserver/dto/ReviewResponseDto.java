package ru.fedbon.userserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ReviewResponseDto {

    @NotBlank
    private String id;

    @NotBlank
    private String text;

    private Mono<BookResponseDto> review;

    private String timeAgo;

    private int likeCount;

    private int dislikeCount;
}
