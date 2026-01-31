package com.bookswap.backend.dto;

public class CreateSwapRequestDTO {
    private Long requesterId;
    private Long bookId;

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
}
