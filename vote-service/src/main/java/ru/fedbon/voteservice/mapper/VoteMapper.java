package ru.fedbon.voteservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.fedbon.voteservice.dto.VoteDto;
import ru.fedbon.voteservice.model.Vote;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Mapper(componentModel = "spring")
public interface VoteMapper {

    VoteDto map(Vote vote);

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
    default void calculateTimeAgo(@MappingTarget VoteDto voteDto, Vote vote) {
        voteDto.setTimeAgo(calculateTimeAgo(vote.getCreatedAt()));
    }

    @InheritInverseConfiguration
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Vote map(VoteDto dto);
}