package com.example.ebookstoreserver.repositories;

import com.example.ebookstoreserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProvider(
            String email,
            User.AuthProvider provider
    );
}

