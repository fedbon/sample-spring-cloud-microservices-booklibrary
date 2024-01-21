package ru.fedbon.bookservice.mapper;

import org.springframework.stereotype.Component;
import ru.fedbon.bookservice.dto.GenreDto;
import ru.fedbon.bookservice.model.Genre;


@Component
public class GenreMapper {

    private GenreMapper() {
    }

    public static GenreDto mapGenreToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}

