package ru.fedbon.authorservice.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.fedbon.authorservice.model.VoteByUser;
import ru.fedbon.authorservice.model.Author;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@ChangeLog(order = "001")
public class DatabaseChangelog {

    private final Random random = new Random();

    private final Author author1 = new Author("1", "Лев Толстой", generateVotesList(10));

    private final Author author2 = new Author("2", "Федор Достоевский", generateVotesList(4));

    private final Author author3 = new Author("3", "Станислав Лем", generateVotesList(1));

    private final Author author4 = new Author("4", "Михаил Булгаков", generateVotesList(21));

    private final Author author5 = new Author("5", "Дмитрий Глуховский", generateVotesList(2));

    private final Author author6 = new Author("6", "Франц Кафка", generateVotesList(14));

    private List<VoteByUser> generateVotesList(int count) {
        List<VoteByUser> votesList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            boolean isEnabled = random.nextBoolean();
            votesList.add(new VoteByUser(String.valueOf(i), isEnabled));
        }

        return votesList;
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
