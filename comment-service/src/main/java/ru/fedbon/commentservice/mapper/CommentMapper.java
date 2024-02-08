package ru.fedbon.commentservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.fedbon.commentservice.dto.CommentByBookDto;
import ru.fedbon.commentservice.dto.CommentDto;
import ru.fedbon.commentservice.model.Comment;
import ru.fedbon.commentservice.model.VoteByUser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "positiveVotesCount", expression = "java(countPositiveVotes(comment))")
    @Mapping(target = "negativeVotesCount", expression = "java(countNegativeVotes(comment))")
    CommentDto mapToCommentDto(Comment comment);

    @Mapping(target = "positiveVotesCount", expression = "java(countPositiveVotes(comment))")
    @Mapping(target = "negativeVotesCount", expression = "java(countNegativeVotes(comment))")
    CommentByBookDto mapToCommentByBookDto(Comment comment);

    default String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutesAgo = ChronoUnit.MINUTES.between(createdAt, now);
            return minutesAgo + " minutes ago";
        } else {
            return "N/A";
        }
    }

    @AfterMapping
    default void calculateTimeAgo(@MappingTarget CommentByBookDto commentByBookDto, Comment comment) {
        commentByBookDto.setTimeAgo(calculateTimeAgo(comment.getCreatedAt()));
    }

    @AfterMapping
    default void calculateTimeAgo(@MappingTarget CommentDto commentDto, Comment comment) {
        commentDto.setTimeAgo(calculateTimeAgo(comment.getCreatedAt()));
    }

    default int countPositiveVotes(Comment comment) {
        return (int) comment.getVoteByUserList().stream().filter(VoteByUser::isPositive).count();
    }

    default int countNegativeVotes(Comment comment) {
        return (int) comment.getVoteByUserList().stream().filter(vote -> !vote.isPositive()).count();
    }
}