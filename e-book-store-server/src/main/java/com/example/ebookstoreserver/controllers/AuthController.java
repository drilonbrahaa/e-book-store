package com.example.ebookstoreserver.controllers;


import com.example.ebookstoreserver.dto.auth.AuthResponse;
import com.example.ebookstoreserver.dto.auth.LoginRequest;
import com.example.ebookstoreserver.dto.auth.RegisterRequest;
import com.example.ebookstoreserver.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest, response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(authService.register(registerRequest, response));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(authService.refresh(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
}

