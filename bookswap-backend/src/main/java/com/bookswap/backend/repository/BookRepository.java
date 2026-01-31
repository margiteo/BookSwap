package com.bookswap.backend.repository;

import com.bookswap.backend.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findAllByOwner_Id(Long ownerId);

    List<BookEntity> findAllByOwner_IdNot(Long ownerId); // dacă o folosești la catalog
}
