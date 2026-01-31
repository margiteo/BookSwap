package com.bookswap.backend.controller;

import com.bookswap.backend.dto.BookDTO;
import com.bookswap.backend.entity.BookEntity;
import com.bookswap.backend.repository.BookRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {

    private final BookRepository bookRepo;

    public BookController(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    // Ex: /api/books/catalog?requesterId=1
    @GetMapping("/catalog")
    public List<BookDTO> catalog(@RequestParam(required = false) Long requesterId) {
        return bookRepo.findAll().stream()
                .filter(b -> requesterId == null || !b.getOwner().getId().equals(requesterId))
                .map(b -> new BookDTO(
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getGenre(),
                        b.getOwner().getUsername()
                ))
                .toList();
    }
    @GetMapping("/my")
    public List<BookDTO> myBooks(@RequestParam Long userId) {
        return bookRepo.findAllByOwner_Id(userId).stream()
                .map(b -> new BookDTO(b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(), b.getOwner().getUsername()))
                .toList();
    }


}
