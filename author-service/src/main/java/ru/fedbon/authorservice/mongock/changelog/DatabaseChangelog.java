package ru.fedbon.authorservice.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.fedbon.authorservice.model.Author;


import java.util.List;
import java.util.Random;


@ChangeLog(order = "001")
public class DatabaseChangelog {

    private final Random random = new Random();

    private final Author author1 = new Author("1", "Лев Толстой", generateRandomCount());

    private final Author author2 = new Author("2", "Федор Достоевский", generateRandomCount());

    private final Author author3 = new Author("3", "Станислав Лем", generateRandomCount());

    private final Author author4 = new Author("4", "Михаил Булгаков", generateRandomCount());

    private final Author author5 = new Author("5", "Дмитрий Глуховский", generateRandomCount());

    private final Author author6 = new Author("6", "Франц Кафка", generateRandomCount());

    private int generateRandomCount() {
        return random.nextInt(101);
    }

    @ChangeSet(order = "000", id = "dropDB", author = "fedbon", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertAuthors", author = "fedbon")
    public void insertAuthors(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(author1, author2, author3, author4, author5, author6));
    }
}
