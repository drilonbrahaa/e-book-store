package com.example.ebookstoreserver.repositories;

import com.example.ebookstoreserver.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    @Query("""
                SELECT b FROM Book b
                JOIN b.categories c
                WHERE c.name = :category
            """)
    Page<Book> findByCategory(
            @Param("category") String category,
            Pageable pageable
    );
}

