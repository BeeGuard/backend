package com.example.pnapibackend.security.jwt;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.security.service.UserDetailsImpl;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Log4j2
@Component
public class JwtTokenProvider {

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    final private SecretKey privateKey;

    public JwtTokenProvider(@Value("${app.jwtSecret}") String jwtSecret) {
        byte[] keyBytes = Arrays.copyOf(jwtSecret.getBytes(StandardCharsets.UTF_8), 64); // Ensure 64 bytes
        privateKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.HS512)
                .compact();
    }

    public String generateToken(Account account) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(account.getEmail())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.HS512)
                .compact();
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(privateKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(privateKey).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            log.log(Level.INFO, "Error while verifying signature");
        } catch (MalformedJwtException ex) {
            log.log(Level.INFO, "Malformed Jwt");
        } catch (ExpiredJwtException ex) {
            log.log(Level.INFO, "Session expired");
        } catch (UnsupportedJwtException ex) {
            log.log(Level.INFO, "Unsupported JWT");
        } catch (IllegalArgumentException ex) {
            log.log(Level.INFO, "Unknown error while parsing JWT");
        }
        return false;
    }
}
