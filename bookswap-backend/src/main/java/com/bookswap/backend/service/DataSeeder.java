package com.bookswap.backend.service;

import com.bookswap.backend.entity.BookEntity;
import com.bookswap.backend.entity.UserEntity;
import com.bookswap.backend.repository.BookRepository;
import com.bookswap.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepo,
                      BookRepository bookRepo,
                      PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepo.count() > 0) return;

        UserEntity ana = new UserEntity();
        ana.setUsername("Ana");
        ana.setEmail("ana@test.com");
        ana.setPasswordHash(passwordEncoder.encode("1234"));

        UserEntity maria = new UserEntity();
        maria.setUsername("Maria");
        maria.setEmail("maria@test.com");
        maria.setPasswordHash(passwordEncoder.encode("1234"));

        userRepo.saveAll(List.of(ana, maria));

        BookEntity b1 = new BookEntity("Harry Potter", "J.K. Rowling", "Fantasy", ana);
        BookEntity b2 = new BookEntity("Dune", "Frank Herbert", "SF", maria);

        bookRepo.saveAll(List.of(b1, b2));
    }
}
