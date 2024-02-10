package ru.fedbon.voteservice.mapper;

import org.mapstruct.Mapper;
import ru.fedbon.voteservice.dto.VoteAuthorRequest;
import ru.fedbon.voteservice.dto.VoteBookRequest;
import ru.fedbon.voteservice.dto.VoteCommentRequest;
import ru.fedbon.voteservice.model.VoteAuthor;
import ru.fedbon.voteservice.model.VoteBook;
import ru.fedbon.voteservice.model.VoteComment;


@Mapper(componentModel = "spring")
public interface VoteMapper {

    VoteAuthor mapToVoteAuthor(VoteAuthorRequest voteAuthorRequest);

    VoteBook mapToVoteBook(VoteBookRequest voteBookRequest);

    VoteComment mapToVoteComment(VoteCommentRequest voteCommentRequest);
}