package me.risinu.jobportal.util;

import io.jsonwebtoken.*;
import me.risinu.jobportal.dto.UsersDto;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class JWT {

    private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;
    SecretKey key = Jwts.SIG.HS256.key().build();

    public JWT(ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
        this.clientHttpRequestFactorySettings = clientHttpRequestFactorySettings;
    }

    public String generateToken(UsersDto user) {
        long EXPIRATION_TIME = 1000 * 60 * 60;

        return Jwts.builder()
                // Use subject for userId; keep email in a separate claim.
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Verifies token signature + expiry only.
     */
    public boolean verifyToken(String token) {
        return parseClaims(token).isPresent();
    }

    /**
     * Verifies token signature + expiry AND that it belongs to the provided userId.
     */
    public boolean verifyTokenOwnedByUser(String token, int userId) {
        return extractUserId(token)
                .map(id -> id == userId)
                .orElse(false);
    }

    public Optional<Integer> extractUserId(String token) {
        return parseClaims(token)
                .map(jws -> jws.getPayload().getSubject())
                .flatMap(subject -> {
                    try {
                        return Optional.of(Integer.parseInt(subject));
                    } catch (NumberFormatException ex) {
                        return Optional.empty();
                    }
                });
    }

    public Optional<String> extractEmail(String token) {
        return parseClaims(token)
                .map(jws -> jws.getPayload().get("email", String.class));
    }

    private Optional<Jws<Claims>> parseClaims(String token) {
        try {
            String raw = normalize(token);
            if (raw == null || raw.isBlank()) {
                return Optional.empty();
            }

            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(raw);
            return Optional.of(claims);
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Accepts both raw tokens and "Bearer <token>" strings.
     */
    private String normalize(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }
}
