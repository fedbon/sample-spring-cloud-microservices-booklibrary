package ru.fedbon.bookservice.mapper;

import org.springframework.stereotype.Component;
import ru.fedbon.bookservice.dto.BookCreateDto;
import ru.fedbon.bookservice.dto.BookDto;
import ru.fedbon.bookservice.model.Author;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.model.Genre;


@Component
public class BookMapper {

    private BookMapper() {
    }

    public static Book mapDtoToNewBook(BookCreateDto bookCreateDto, Genre genre, Author author) {

        var bookBuilder = Book.builder();
        bookBuilder.title(bookCreateDto.getTitle());
        bookBuilder.genre(genre);
        bookBuilder.author(author);

        return bookBuilder.build();
    }

    public static BookDto mapBookToDto(Book book) {

        var bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor().getName());
        bookDto.setGenre(book.getGenre().getName());

        return bookDto;
    }

}
