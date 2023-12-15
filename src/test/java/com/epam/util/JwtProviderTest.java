package com.epam.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @Mock
    private HttpServletRequest request;

    private JwtProvider jwtProvider;


    @BeforeEach
    public void setUp() {
        jwtProvider = new JwtProvider("yourSecretKey", 900L);
    }

    @Test
    void resolveClaims_ValidToken_ShouldReturnClaims() {// Arrange
        String validToken = jwtProvider.generateToken("testUser");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        Claims claims = jwtProvider.resolveClaims(request);

        assertNotNull(claims);
        assertEquals("testUser", claims.getSubject());
    }

    @Test
    void resolveClaims_InvalidToken_ShouldThrowException() {
        String invalidToken = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

        assertThrows(Exception.class, () -> jwtProvider.resolveClaims(request));
    }

    @Test
    void invalidateToken_ShouldAddTokenToBlacklist() {
        String tokenToInvalidate = "invalidToken";

        jwtProvider.invalidateToken(tokenToInvalidate);

        assertTrue(jwtProvider.isTokenBlacklisted(tokenToInvalidate));
    }
}
