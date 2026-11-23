package com.foodrescue.security.jwt;

import com.foodrescue.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** JWT contract: sub = user id, claims: email, role. HS256, expiry from app.jwt.expiration-minutes. */
@Service
public class JwtService {

    public static final String VERSION_CLAIM = "ver";

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        // hmacShaKeyFor throws WeakKeyException for secrets shorter than 256 bits -> fail fast on bad config
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim(VERSION_CLAIM, user.getTokenVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /** Token version the token was issued with, or -1 when the claim is missing. */
    public static int versionOf(Claims claims) {
        Object v = claims.get(VERSION_CLAIM);
        return v instanceof Number n ? n.intValue() : -1;
    }

    /** @throws io.jsonwebtoken.JwtException if the token is invalid, tampered with or expired */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
