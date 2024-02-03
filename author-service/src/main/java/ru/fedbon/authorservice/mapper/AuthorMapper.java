package ru.fedbon.authorservice.mapper;


import org.mapstruct.Mapper;
import ru.fedbon.authorservice.dto.AuthorDto;
import ru.fedbon.authorservice.model.Author;


@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto map(Author author);
}