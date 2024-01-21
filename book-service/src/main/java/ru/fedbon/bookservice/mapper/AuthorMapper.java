package ru.fedbon.bookservice.mapper;

import org.springframework.stereotype.Component;
import ru.fedbon.bookservice.dto.AuthorDto;
import ru.fedbon.bookservice.model.Author;

@Component
public class AuthorMapper {

    private AuthorMapper() {
    }

    public static AuthorDto mapAuthorToDto(Author author) {
        return new AuthorDto(author.getId(), author.getName());
    }
}
