package ru.fedbon.bookservice.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.fedbon.bookservice.model.Author;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.model.Comment;
import ru.fedbon.bookservice.model.Genre;

import java.util.List;


@ChangeLog(order = "001")
public class DatabaseChangelog {

    private final Author author1 = new Author("65721548456e6853487f2f31", "Лев Толстой");

    private final Author author2 = new Author("65721548456e6853487f2f32", "Николай Гоголь");

    private final Author author3 = new Author("65721548456e6853487f2f33", "Фёдор Достоевский");

    private final Genre genre1 = new Genre("65721548456e6853487f2f21", "Русская классика");

    private final Genre genre2 = new Genre("65721548456e6853487f2f22", "Фантастическая литература");

    private final Genre genre3 = new Genre("65721548456e6853487f2f23", "Сатира");

    private final Book book1 = new Book("65721548456e6853487f2f41", "Война и мир", genre1, author1);

    private final Book book2 = new Book("65721548456e6853487f2f42", "Преступление и наказание", genre1, author3);

    private final Book book3 = new Book("65721548456e6853487f2f43", "Идиот", genre3, author3);

    private final Comment comment1 = new Comment("65721548456e6853487f2f51", "Комментарий 1 к книге 1", book1);

    private final Comment comment2 = new Comment("65721548456e6853487f2f52", "Комментарий 1 к книге 2", book2);

    private final Comment comment3 = new Comment("65721548456e6853487f2f53", "Комментарий 1 к книге 3", book3);

    private final Comment comment4 = new Comment("65721548456e6853487f2f54", "Комментарий 2 к книге 3", book3);


    @ChangeSet(order = "000", id = "dropDB", author = "fedbon", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertAuthors", author = "fedbon")
    public void insertAuthors(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(author1, author2, author3));
    }

    @ChangeSet(order = "002", id = "insertGenres", author = "fedbon")
    public void insertGenres(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(genre1, genre2, genre3));
    }

    @ChangeSet(order = "003", id = "insertBook", author = "fedbon")
    public void insertBooks(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(book1, book2, book3));
    }

    @ChangeSet(order = "004", id = "insertComment", author = "fedbon")
    public void insertComments(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(comment1, comment2, comment3, comment4));
    }
}
