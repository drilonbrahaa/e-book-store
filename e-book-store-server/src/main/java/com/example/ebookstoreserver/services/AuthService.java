package com.example.ebookstoreserver.services;

import com.example.ebookstoreserver.dto.auth.AuthResponse;
import com.example.ebookstoreserver.dto.auth.LoginRequest;
import com.example.ebookstoreserver.dto.auth.RegisterRequest;
import com.example.ebookstoreserver.entities.Role;
import com.example.ebookstoreserver.entities.User;
import com.example.ebookstoreserver.jwt.JwtService;
import com.example.ebookstoreserver.repositories.UserRepository;
import com.example.ebookstoreserver.userdetails.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),
                        loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new RuntimeException("User not found!"));
        Set<String> roles = user.getRoles().stream().map(r -> r.getName().toString()).collect(Collectors.toSet());
        String accessToken = addTokenCookie(response, userDetails);

        return new AuthResponse(accessToken, roles);
    }

    public AuthResponse register(RegisterRequest registerRequest, HttpServletResponse response) {
        if (registerRequest.email().isBlank() || registerRequest.password().isBlank() || registerRequest.fullName().isBlank()) {
            throw new RuntimeException("Fields must not be blank!");
        }

        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new RuntimeException("Email taken!");
        }

        User user = new User();
        user.setEmail(registerRequest.email());
        user.setFullName(registerRequest.fullName());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = addTokenCookie(response, userDetails);

        return new AuthResponse(accessToken, Set.of(Role.RoleType.USER.toString()));
    }

    public AuthResponse refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Missing refresh token!"));

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Invalid refresh token!");
        }

        CustomUserDetails userDetails = new CustomUserDetails(userRepository
                .findByEmail(jwtService.getUsername(refreshToken)).orElseThrow(() -> new RuntimeException("User not found!")));

        String token = jwtService.generateAccessToken(userDetails);
        Set<String> roles = userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.toSet());

        return new AuthResponse(token, roles);
    }

    private String addTokenCookie(HttpServletResponse response, CustomUserDetails userDetails) {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        String accessToken = jwtService.generateAccessToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return accessToken;
    }
}

