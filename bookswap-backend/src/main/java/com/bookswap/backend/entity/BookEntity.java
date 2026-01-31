package com.bookswap.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class BookEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String title;
    @Column(nullable = false) private String author;
    @Column(nullable = false) private String genre;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    public BookEntity() {}

    public BookEntity(String title, String author, String genre, UserEntity owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public UserEntity getOwner() { return owner; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setOwner(UserEntity owner) { this.owner = owner; }

}
