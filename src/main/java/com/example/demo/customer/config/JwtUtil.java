package com.example.demo.customer.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    // Change this secret in production — min 32 chars
    private static final String SECRET = "MySuperSecretKeyForJwtSigning1234567890!!";
    private static final long EXPIRY_MS = 1000L * 60 * 60 * 24; // 24 hours

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ── Generate ─────────────────────────────────────────────────────

    public String generateToken(UUID userId, UUID orgId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId.toString())
                .claim("orgId",  orgId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Parse ────────────────────────────────────────────────────────

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public UUID getOrgId(String token) {
        return UUID.fromString((String) parseClaims(token).get("orgId"));
    }

    public UUID getUserId(String token) {
        return UUID.fromString((String) parseClaims(token).get("userId"));
    }
}