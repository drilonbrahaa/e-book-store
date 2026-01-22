package com.example.ebookstoreserver.repositories;

import com.example.ebookstoreserver.entities.UserLibrary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLibraryRepository
        extends JpaRepository<UserLibrary, Long> {

    boolean existsByUserIdAndBookId(
            Long userId,
            Long bookId
    );

    Page<UserLibrary> findByUserId(
            Long userId,
            Pageable pageable
    );
}

