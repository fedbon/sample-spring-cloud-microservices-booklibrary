package ru.fedbon.commentservice.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.fedbon.commentservice.model.Comment;
import ru.fedbon.commentservice.model.VoteByUser;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@ChangeLog(order = "001")
public class DatabaseChangelog {

    private final Random random = new Random();

    private final Comment comment1 = new Comment("1", "Потрясающая книга, особенно понравились персонажи, " +
            "так прописаны, они буквально раскрываются нам с каждой главой.",
            "1","1",
            "jack_bore", LocalDateTime.now().minusMinutes(5L),
            generateVotesList(2));

    private final Comment comment2 = new Comment("2", "Потрясающее продолжение первой части… " +
            "Сказать, что я в восторге, - это по сути ничего не сказать.",
            "2", "1",
            "jack_bore", LocalDateTime.now().minusMinutes(10L),
            generateVotesList(4));

    private final Comment comment3 = new Comment("3", "Невероятная атмосфера и захватывающий сюжет. " +
            "Очень рекомендую всем любителям фэнтези.",
            "3", "1",
            "jack_bore", LocalDateTime.now().minusMinutes(15L),
            generateVotesList(8));

    private final Comment comment4 = new Comment("4", "Очень интересный поворот событий! " +
            "Не мог оторваться до последней страницы.",
            "4","1",
            "jack_bore", LocalDateTime.now().minusMinutes(20L),
            generateVotesList(1));

    private final Comment comment5 = new Comment("5", "Замечательная книга! Особенно понравился финал.",
            "1", "2",
            "book_lover", LocalDateTime.now().minusMinutes(25L),
            generateVotesList(3));

    private final Comment comment6 = new Comment("6", "Не могла оторваться. Все персонажи такие живые!",
            "1", "3",
            "avid_reader", LocalDateTime.now().minusMinutes(30L),
            generateVotesList(4));

    private final Comment comment7 = new Comment("7", "Отличное продолжение серии!",
            "2", "4",
            "fantasy_fan", LocalDateTime.now().minusMinutes(35L),
            generateVotesList(10));

    private final Comment comment8 = new Comment("8", "Фантастическая атмосфера!",
            "3", "5",
            "mystery_lover", LocalDateTime.now().minusMinutes(40L),
            generateVotesList(12));

    private final Comment comment9 = new Comment("9", "Сильный сюжет, рекомендую!",
            "4", "3",
            "avid_reader", LocalDateTime.now().minusMinutes(45L),
            generateVotesList(21));

    private List<VoteByUser> generateVotesList(int count) {
        List<VoteByUser> votesList = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            boolean isPositive = random.nextBoolean();
            var voteByUser = new VoteByUser(String.valueOf(i), isPositive);
            votesList.add(voteByUser);
        }

        return votesList;
    }

    @ChangeSet(order = "000", id = "dropDB", author = "fedbon", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertReviews", author = "fedbon")
    public void insertReviews(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(comment1, comment2, comment3, comment4, comment5, comment6,
                comment7, comment8, comment9));
    }
}
