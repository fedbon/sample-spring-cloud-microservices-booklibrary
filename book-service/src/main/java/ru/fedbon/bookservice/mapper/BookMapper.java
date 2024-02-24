package ru.fedbon.bookservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.fedbon.bookservice.dto.BookWithAuthorInfoAndCommentsDto;
import ru.fedbon.bookservice.dto.BookWithAuthorInfoDto;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.model.Genre;
import ru.fedbon.bookservice.model.VoteByUser;


@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "rating", expression = "java(calculateRating(book))")
    BookWithAuthorInfoDto mapToBookWithAuthorsDto(Book book);

    @Mapping(target = "genre", expression = "java(mapGenre(book.getGenre()))")
    @Mapping(target = "rating", expression = "java(calculateRating(book))")
    @Mapping(target = "positiveVotesCount", expression = "java(countPositiveVotes(book))")
    @Mapping(target = "negativeVotesCount", expression = "java(countNegativeVotes(book))")
    BookWithAuthorInfoAndCommentsDto mapToBookWithAuthorsAndCommentsDto(Book book);

    default String mapGenre(Genre genre) {
        return (genre != null) ? genre.getName() : null;
    }

    default double calculateRating(Book book) {
        double positiveVotes = countPositiveVotes(book);
        double totalVotes = positiveVotes + countNegativeVotes(book);

        if (totalVotes == 0) {
            return 0.0;
        }

        double rating = positiveVotes / totalVotes * 10.0;
        return Math.round(rating * 10.0) / 10.0;
    }

    default int countPositiveVotes(Book book) {
        return (int) book.getVoteByUserList().stream().filter(VoteByUser::isPositive).count();
    }

    default int countNegativeVotes(Book book) {
        return (int) book.getVoteByUserList().stream().filter(vote -> !vote.isPositive()).count();
    }
}