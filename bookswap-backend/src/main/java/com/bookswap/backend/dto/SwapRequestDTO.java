package com.bookswap.backend.dto;

public class SwapRequestDTO {

    private Long requestId;
    private String requesterUsername;
    private String bookTitle;
    private String bookAuthor;
    private String status;

    public SwapRequestDTO(
            Long requestId,
            String requesterUsername,
            String bookTitle,
            String bookAuthor,
            String status
    ) {
        this.requestId = requestId;
        this.requesterUsername = requesterUsername;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.status = status;
    }

    public Long getRequestId() { return requestId; }
    public String getRequesterUsername() { return requesterUsername; }
    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public String getStatus() { return status; }
}
