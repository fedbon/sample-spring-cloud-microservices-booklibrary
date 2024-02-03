package ru.fedbon.commentservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.fedbon.commentservice.dto.CommentDto;
import ru.fedbon.commentservice.model.Comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto map(Comment comment);

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
    default void calculateTimeAgo(@MappingTarget CommentDto commentDto, Comment comment) {
        commentDto.setTimeAgo(calculateTimeAgo(comment.getCreatedAt()));
    }

    @InheritInverseConfiguration
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Comment map(CommentDto dto);
}