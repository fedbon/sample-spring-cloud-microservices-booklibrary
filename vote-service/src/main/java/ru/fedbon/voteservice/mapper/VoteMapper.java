package ru.fedbon.voteservice.mapper;

import org.mapstruct.Mapper;
import ru.fedbon.voteservice.dto.VoteAuthorRequest;
import ru.fedbon.voteservice.model.VoteAuthor;


@Mapper(componentModel = "spring")
public interface VoteMapper {

    VoteAuthor mapToVoteAuthor(VoteAuthorRequest voteAuthorRequest);
}