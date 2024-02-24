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

import static ru.fedbon.bookservice.constants.AppConstants.NAME;
import static ru.fedbon.bookservice.constants.Message.SUCCESSFULLY_RETRIEVED_GENRES_MESSAGE;
import static ru.fedbon.bookservice.constants.PathConstants.API_V1_GENRES;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_GENRES)
public class GenreRestController {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @GetMapping
    public Flux<GenreDto> handleGetAll() {
        return genreRepository.findAll(Sort.by(Sort.Direction.ASC, NAME))
                .map(genreMapper::map)
                .doOnNext(genreDto -> log.info(SUCCESSFULLY_RETRIEVED_GENRES_MESSAGE, genreDto.getId()));
    }
}


