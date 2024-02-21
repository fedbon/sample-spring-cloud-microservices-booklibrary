package ru.fedbon.bookservice.dto;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BookWithAuthorInfoDto {

    private String id;

    private String title;

    private String authorName;

    private String description;

    private String createdAt;

    private Double rating;
}

