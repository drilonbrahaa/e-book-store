package com.example.ebookstoreserver.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_libraries",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "book_id"}
        )
)
@Getter
@Setter
public class UserLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Book book;

    private LocalDateTime purchasedAt;
}
