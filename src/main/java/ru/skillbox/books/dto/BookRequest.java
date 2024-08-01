package ru.skillbox.books.dto;

import lombok.Data;

@Data
public class BookRequest {

    private String title;

    private String author;

    private String category;

}
