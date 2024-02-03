package ru.fedbon.userserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private String username;

    private Flux<BookResponseDto> userLibraryDto;

    private Mono<ReviewResponseDto> review;

    private int reviewsCount;

    private int booksReadCount;

    private int booksLikedCount;

    private int booksDislikedCount;
}
