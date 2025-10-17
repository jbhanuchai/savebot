package com.savebot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Use 32+ chars for HS256 (store securely via config for real apps)
    private static final String SECRET = "replace-with-32+char-super-secret-key-xxxxxxxxxxxxxx";
    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /** Basic token with just subject */
    public String generateToken(String username) {
        return generateToken(username, null);
    }

    /** Token including roles claim (matches your AuthService call) */
    public String generateToken(String username, Set<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_MS);

        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(signingKey(), Jwts.SIG.HS256); // 0.12.x style

        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    public String extractUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    /** Backwards-compat alias for your filter */
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    public boolean validate(String token) {
        try {
            parseAllClaims(token); // throws if invalid/expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey()) // requires SecretKey in 0.12.x
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
