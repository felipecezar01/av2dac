package com.av2dac.util;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final String secret = "YourSecretKeyYourSecretKeyYourSecretKeyYourSecretKey";
    private final long expiration = 1000; // 1 segundo para testar a expiração

    private JwtUtil jwtUtil = new JwtUtil(secret, expiration);

    @Test
    void generateToken_Valid() {
        // Arrange
        String username = "test@example.com";

        // Act
        String token = jwtUtil.generateToken(username);

        // Assert
        assertNotNull(token);
    }

    @Test
    void validateToken_ValidToken() {
        // Arrange
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // Act
        boolean isValid = jwtUtil.validateToken(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken() throws InterruptedException {
        // Arrange
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // Esperar o token expirar (1.5 segundos)
        Thread.sleep(1500);

        // Act
        boolean isValid = jwtUtil.validateToken(token, username);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsername_ValidToken() {
        // Arrange
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractUsername_InvalidToken() {
        // Arrange
        String token = "invalid.token.here";

        // Act & Assert
        assertThrows(io.jsonwebtoken.JwtException.class, () -> {
            jwtUtil.extractUsername(token);
        });
    }
}
