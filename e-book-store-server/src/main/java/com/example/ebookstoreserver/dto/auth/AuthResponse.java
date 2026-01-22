package com.example.ebookstoreserver.dto.auth;

import java.util.Set;

public record AuthResponse(String accessToken, Set<String> role) {
}
