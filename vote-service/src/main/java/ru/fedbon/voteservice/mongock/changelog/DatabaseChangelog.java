package ru.fedbon.voteservice.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.fedbon.voteservice.model.Vote;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@ChangeLog(order = "001")
public class DatabaseChangelog {

    private final Random random = new Random();

    private final Vote vote1 = new Vote("1", "Потрясающая книга, особенно понравились персонажи, " +
            "так прописаны, они буквально раскрываются нам с каждой главой.",
            "1", "Песнь Сорокопута", "Мосян Тунсю","1",
            "jack_bore", LocalDateTime.now().minusMinutes(5L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote2 = new Vote("2", "Потрясающее продолжение первой части… " +
            "Сказать, что я в восторге, - это по сути ничего не сказать.",
            "2", "Благословение небожителей. Том 2", "Мосян Тунсю",
            "1", "jack_bore", LocalDateTime.now().minusMinutes(10L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote3 = new Vote("3", "Невероятная атмосфера и захватывающий сюжет. " +
            "Очень рекомендую всем любителям фэнтези.",
            "3", "Тайна Затерянного Города", "Примаченко Ольга",
            "1", "jack_bore", LocalDateTime.now().minusMinutes(15L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote4 = new Vote("4", "Очень интересный поворот событий! " +
            "Не мог оторваться до последней страницы.",
            "4", "Спасение Эльфийского Королевства", "Клейсон Джордж",
            "1", "jack_bore", LocalDateTime.now().minusMinutes(20L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote5 = new Vote("5", "Замечательная книга! Особенно понравился финал.",
            "1", "Песнь Сорокопута", "Стрелеки Джон",
            "2", "book_lover", LocalDateTime.now().minusMinutes(25L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote6 = new Vote("6", "Не могла оторваться. Все персонажи такие живые!",
            "1", "Песнь Сорокопута", "Маслова Анна",
            "3", "avid_reader", LocalDateTime.now().minusMinutes(30L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote7 = new Vote("7", "Отличное продолжение серии!",
            "2", "Благословение небожителей. Том 2", "Примаченко Ольга",
            "2", "fantasy_fan", LocalDateTime.now().minusMinutes(35L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote8 = new Vote("8", "Фантастическая атмосфера!",
            "3", "Тайна Затерянного Города", "Мосян Тунсю",
            "2", "mystery_lover", LocalDateTime.now().minusMinutes(40L),
            generateRandomCount(), generateRandomCount());

    private final Vote vote9 = new Vote("9", "Сильный сюжет, рекомендую!",
            "4", "Спасение Эльфийского Королевства", "Синсеро Джен",
            "2", "avid_reader", LocalDateTime.now().minusMinutes(45L),
            generateRandomCount(), generateRandomCount());

    private int generateRandomCount() {
        return random.nextInt(101);
    }

    @ChangeSet(order = "000", id = "dropDB", author = "fedbon", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertReviews", author = "fedbon")
    public void insertReviews(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(vote1, vote2, vote3, vote4, vote5, vote6,
                vote7, vote8, vote9));
    }
}
