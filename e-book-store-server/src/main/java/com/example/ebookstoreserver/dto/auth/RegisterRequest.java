package com.example.ebookstoreserver.dto.auth;

public record RegisterRequest(String email, String password, String fullName) {
}
