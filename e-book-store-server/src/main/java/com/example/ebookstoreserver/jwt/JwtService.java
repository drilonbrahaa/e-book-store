package com.example.ebookstoreserver.jwt;

import com.example.ebookstoreserver.userdetails.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(@Value("${PRK_PATH}") String privatePath, @Value("${PUK_PATH}") String publicPath) throws Exception {
        this.privateKey = KeyLoader.loadPrivateKey(privatePath);
        this.publicKey = KeyLoader.loadPublicKey(publicPath);
    }

    public String generateAccessToken(CustomUserDetails customUserDetails) {
        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        long accessTokenExpiry = 1000 * 60 * 15;
        return Jwts.builder()
                .claim("roles", roles)
                .subject(customUserDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .signWith(privateKey)
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails customUserDetails) {
        long refreshTokenExpiry = 1000 * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .subject(customUserDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
                .signWith(privateKey)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parser().verifyWith(publicKey)
                .build().parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public List<String> getRoles(String token) {
        Object o = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().get("roles");
        if (o instanceof List<?> roles) {
            return roles.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }
}

