package me.risinu.jobportal.util;

import io.jsonwebtoken.*;
import me.risinu.jobportal.dto.UsersDto;
import me.risinu.jobportal.entity.Users;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class  JWT{

    private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;
    SecretKey key = Jwts.SIG.HS256.key().build();

    public JWT(ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
        this.clientHttpRequestFactorySettings = clientHttpRequestFactorySettings;
    }

    public String generateToken(UsersDto user) {
        long EXPIRATION_TIME = 1000 * 60 * 60;

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }
}
