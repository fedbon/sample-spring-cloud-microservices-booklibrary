package ru.fedbon.bookservice.mapper;


import org.mapstruct.Mapper;
import ru.fedbon.bookservice.dto.GenreDto;
import ru.fedbon.bookservice.model.Genre;



@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDto map(Genre genre);
}