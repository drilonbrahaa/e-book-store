package com.example.ebookstoreserver.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @Enumerated(EnumType.STRING)
    private RoleType name;

    public enum RoleType {
        USER, ADMIN
    }
}

