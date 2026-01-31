package com.bookswap.backend.dto;

public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String genre;
    private String ownerUsername;

    public BookDTO(Long id, String title, String author, String genre, String ownerUsername) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getOwnerUsername() { return ownerUsername; }
}
