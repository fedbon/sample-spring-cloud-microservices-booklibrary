package ru.fedbon.bookservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.fedbon.bookservice.dto.GenreDto;
import ru.fedbon.bookservice.mapper.GenreMapper;
import ru.fedbon.bookservice.repository.GenreRepository;



@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;

    @GetMapping(value = "/api/genres")
    public Flux<GenreDto> handleGetAll() {
        return genreRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .map(GenreMapper::mapGenreToDto);
    }
}
