package ru.fedbon.bookservice.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.model.Genre;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@ChangeLog(order = "001")
public class DatabaseChangelog {

    private final Random random = new Random();

    private final Genre genre1 = new Genre("1", "Роман");

    private final Genre genre2 = new Genre("2", "Научная фантастика");

    private final Genre genre3 = new Genre("3", "Философия");

    private final Genre genre4 = new Genre("4", "Приключения");

    private final Genre genre5 = new Genre("5", "Поэзия");

    private final Book book1 = new Book(
            "1",
            "Война и мир",
            genre1, "1",
            "О КНИГЕ «Война и мир» — это великолепный роман Льва Толстого, " +
                    "который охватывает период событий во время Наполеоновских войн. " +
                    "Он рассказывает о судьбах нескольких аристократических семей " +
                    "и затрагивает темы любви, войны, политики и человеческих отношений. " +
                    "Это произведение является важной частью мировой литературной культуры.",
            LocalDateTime.now().minusMinutes(5L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book2 = new Book(
            "2",
            "Преступление и наказание",
            genre1, "2",
            "О КНИГЕ «Преступление и наказание» — это роман Федора Достоевского, в котором исследуется " +
                    "моральное воздействие преступления на сознание человека. " +
                    "Главный герой, Родион Раскольников, погружается в мир внутренней борьбы и пытается " +
                    "найти смысл своего существования в противоречиях морали и человеческой природы.",
            LocalDateTime.now().minusMinutes(10L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book3 = new Book(
            "3",
            "Солярис",
            genre2,
            "3",
            "О КНИГЕ «Солярис» — научно-фантастический роман Станислава Лема. " +
                    "События разворачиваются на загадочной планете Солярис, " +
                    "где главный герой, психолог Крис Кельвин, сталкивается " +
                    "с загадочными явлениями и своими внутренними страхами.",
            LocalDateTime.now().minusMinutes(15L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book4 = new Book(
            "4",
            "Мастер и Маргарита",
            genre3, "4",
            "О КНИГЕ «Мастер и Маргарита» — роман Михаила Булгакова, " +
                    "в котором сочетаются фантастика, аллегория и сатира. " +
                    "Сюжет вращается вокруг визита дьявола в Москву и его взаимодействия " +
                    "с различными обитателями города, а также мастером и его возлюбленной Маргаритой.",
            LocalDateTime.now().minusMinutes(20L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book5 = new Book(
            "5",
            "Метро 2033",
            genre2, "5",
            "О КНИГЕ «Метро 2033» — научно-фантастический роман Дмитрия Глуховского, " +
                    "который описывает жизнь выживших людей в московском метро после ядерной катастрофы. " +
                    "Главный герой, Артем, отправляется в опасное путешествие по подземному миру, " +
                    "сталкиваясь с мутантами и другими опасностями.",
            LocalDateTime.now().minusMinutes(25L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book6 = new Book(
            "6",
            "Процесс",
            genre5, "6",
            "О КНИГЕ «Процесс» — роман Франца Кафки, в котором описывается " +
                    "безымянный герой, подвергшийся странным и бюрократическим процессам " +
                    "по неизвестной причине. Книга затрагивает темы абсурда, человеческой бюрократии " +
                    "и бессмысленности человеческого существования.",
            LocalDateTime.now().minusMinutes(30L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book7 = new Book(
            "7",
            "Идиот",
            genre3, "2",
            "О КНИГЕ «Идиот» — роман Федора Достоевского, в котором рассказывается о приходе " +
                    "принца Мышкина в Петербург после лечения за границей. Он является «идиотом» " +
                    "в смысле отсутствия злобы и желания власти, что приводит к множеству недоразумений и трагедий.",
            LocalDateTime.now().minusMinutes(35L),
            generateRandomCount(),
            generateRandomCount()
    );

    private final Book book8 = new Book(
            "8",
            "Братья Карамазовы",
            genre4, "2",
            "О КНИГЕ «Братья Карамазовы» — последний роман Федора Достоевского, " +
                    "который исследует моральные и философские вопросы через историю трех братьев. " +
                    "Роман затрагивает темы религии, морали, греха и смысла жизни.",
            LocalDateTime.now().minusMinutes(40L),
            generateRandomCount(),
            generateRandomCount()
    );

    private int generateRandomCount() {
        return random.nextInt(101);
    }

    @ChangeSet(order = "000", id = "dropDB", author = "fedbon", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertGenres", author = "fedbon")
    public void insertGenres(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(genre1, genre2, genre3, genre4, genre5));
    }

    @ChangeSet(order = "002", id = "insertBooks", author = "fedbon")
    public void insertBooks(MongockTemplate mongockTemplate) {
        mongockTemplate.insertAll(List.of(book1, book2, book3, book4, book5, book6, book7, book8));
    }
}
