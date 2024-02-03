package ru.fedbon.bookservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.fedbon.bookservice.dto.BookDto;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.model.Genre;


@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "genre", expression = "java(mapGenre(book.getGenre()))")
    @Mapping(target = "rating", expression = "java(calculateRating(book))")
    BookDto map(Book book);

    // Add mapping for List<CommentResponseDto>
    @Mapping(target = "comments", ignore = true)
    BookDto mapToBookDto(Book book);

    default String mapGenre(Genre genre) {
        return (genre != null) ? genre.getName() : null;
    }

    default Double calculateRating(Book book) {
        int totalVotes = book.getPositiveVotesCount() + book.getNegativeVotesCount();

        if (totalVotes == 0) {
            return 0.0;
        }

        double positiveRatio = (double) book.getPositiveVotesCount() / totalVotes;
        double rawRating = positiveRatio * 10.0;

        return Math.round(rawRating * 10.0) / 10.0;
    }
}