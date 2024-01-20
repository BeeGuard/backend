package com.example.pnapibackend.security.jwt;

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

import java.util.Date;
import java.util.UUID;

@Log4j2
@Component
public class JwtTokenProvider {

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    final private Password privateKey;

    public JwtTokenProvider(@Value("${app.jwtSecret}") String jwtSecret) {
        privateKey = Keys.password(jwtSecret.toCharArray());
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.HS512)
                .compact();
    }

    public UUID getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(privateKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().decryptWith(privateKey).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            log.log(Level.DEBUG, "Error while verifying signature");
        } catch (MalformedJwtException ex) {
            log.log(Level.DEBUG, "Malformed Jwt");
        } catch (ExpiredJwtException ex) {
            log.log(Level.DEBUG, "Session expired");
        } catch (UnsupportedJwtException ex) {
            log.log(Level.DEBUG, "Unsupported JWT");
        } catch (IllegalArgumentException ex) {
            log.log(Level.DEBUG, "Unknown error while parsing JWT");
        }
        return false;
    }
}
