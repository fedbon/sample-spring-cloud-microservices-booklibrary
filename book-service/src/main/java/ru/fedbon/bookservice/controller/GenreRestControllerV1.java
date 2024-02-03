package ru.fedbon.bookservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.fedbon.bookservice.dto.GenreDto;
import ru.fedbon.bookservice.mapper.GenreMapper;
import ru.fedbon.bookservice.repository.GenreRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreRestControllerV1 {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @GetMapping
    public Flux<GenreDto> handleGetAll() {
        return genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .map(genreMapper::map)
                .doOnNext(genreDto -> log.info("Genre retrieved successfully. ID: {}", genreDto.getId()))
                .doOnError(error -> log.error("Error occurred while retrieving genres: {}", error.getMessage(), error));
    }
}


