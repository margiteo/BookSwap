package com.bookswap.backend.controller;

import com.bookswap.backend.dto.CreateSwapRequestDTO;
import com.bookswap.backend.dto.SwapRequestDTO;
import com.bookswap.backend.entity.BookEntity;
import com.bookswap.backend.entity.SwapRequestEntity;
import com.bookswap.backend.entity.UserEntity;
import com.bookswap.backend.repository.BookRepository;
import com.bookswap.backend.repository.SwapRequestRepository;
import com.bookswap.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/swaps")
public class SwapController {

    private final SwapRequestRepository swapRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;

    public SwapController(SwapRequestRepository swapRepo, UserRepository userRepo, BookRepository bookRepo) {
        this.swapRepo = swapRepo;
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
    }

    @GetMapping("/inbox/{userId}")
    public List<SwapRequestDTO> inbox(@PathVariable Long userId) {
        return swapRepo
                .findByBook_Owner_IdAndStatus(userId, SwapRequestEntity.Status.PENDING)
                .stream()
                .map(r -> new SwapRequestDTO(
                        r.getId(),
                        r.getRequester().getUsername(),
                        r.getBook().getTitle(),
                        r.getBook().getAuthor(),
                        r.getStatus().name()
                ))
                .toList();
    }

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRequest(@RequestBody CreateSwapRequestDTO dto) {
        UserEntity requester = userRepo.findById(dto.getRequesterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester not found"));

        BookEntity book = bookRepo.findById(dto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (book.getOwner().getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot request your own book");
        }

        SwapRequestEntity req = new SwapRequestEntity();
        req.setRequester(requester);
        req.setBook(book);
        req.setOwner(book.getOwner());   // ⭐ ASTA TREBUIE ADĂUGAT
        req.setStatus(SwapRequestEntity.Status.PENDING);

        swapRepo.save(req);

    }
    @PostMapping("/{requestId}/accept")
    public void accept(@PathVariable Long requestId) {
        SwapRequestEntity req = swapRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (req.getStatus() != SwapRequestEntity.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already handled");
        }

        // transfer cartea
        BookEntity book = req.getBook();
        book.setOwner(req.getRequester());
        bookRepo.save(book);

        req.setStatus(SwapRequestEntity.Status.ACCEPTED);
        swapRepo.save(req);
    }

    @PostMapping("/{requestId}/reject")
    public void reject(@PathVariable Long requestId) {
        SwapRequestEntity req = swapRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (req.getStatus() != SwapRequestEntity.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already handled");
        }

        req.setStatus(SwapRequestEntity.Status.REJECTED);
        swapRepo.save(req);
    }
    @GetMapping("/outbox/{userId}")
    public List<SwapRequestDTO> outbox(@PathVariable Long userId) {
        return swapRepo.findByRequester_Id(userId)
                .stream()
                .map(r -> new SwapRequestDTO(
                        r.getId(),
                        r.getRequester().getUsername(),
                        r.getBook().getTitle(),
                        r.getBook().getAuthor(),
                        r.getStatus().name()
                ))
                .toList();
    }

}
