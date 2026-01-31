package com.bookswap.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "swap_requests")
public class SwapRequestEntity {

    public enum Status { PENDING, ACCEPTED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // cine cere cartea
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    // proprietarul cărții (cel care acceptă/refuză)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    // ce carte se cere
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public SwapRequestEntity() {}

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) this.status = Status.PENDING;
    }

    public Long getId() { return id; }
    public UserEntity getRequester() { return requester; }
    public UserEntity getOwner() { return owner; }
    public BookEntity getBook() { return book; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setRequester(UserEntity requester) { this.requester = requester; }
    public void setOwner(UserEntity owner) { this.owner = owner; }
    public void setBook(BookEntity book) { this.book = book; }
    public void setStatus(Status status) { this.status = status; }
}
