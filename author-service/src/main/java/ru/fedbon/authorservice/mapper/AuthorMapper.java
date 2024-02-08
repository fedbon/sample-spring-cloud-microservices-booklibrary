package ru.fedbon.authorservice.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.fedbon.authorservice.dto.AuthorDto;
import ru.fedbon.authorservice.model.Author;


@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto map(Author author);

    @AfterMapping
    default void calculateVotesCount(@MappingTarget AuthorDto authorDto, Author author) {
        if (author.getVoteByUserList() != null) {
            authorDto.setVotesCount(author.getVoteByUserList().size());
        } else {
            authorDto.setVotesCount(0);
        }
    }
}