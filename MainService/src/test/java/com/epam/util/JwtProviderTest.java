package com.epam.util;

import com.epam.repo.JWTRepo;
import io.jsonwebtoken.Claims;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;

import java.security.Security;

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

    @Mock
    private JWTRepo jwtRepo;

    @BeforeEach
    public void setUp() {
        jwtProvider = new JwtProvider(
                "MIICWgIBAAKBgF88+tpIpZqqk7Fto7hz3ww7uKiK4OJcNFiWA" +
                        "+t81M8xDzAEOhKMOprNdgFMPcPqNoJHiSM2xRbSz9eG3m1Knm9IMDeJH6iWMak4KBTtn6geaU9qdIJtmb" +
                        "+6ekEX9UQdVeKlFMoUQl2CzjMcq2l/ot0bc" +
                        "+JEkPsXUWfQ4P8oIXxbAgMBAAECgYAQu8srrVYDnfMDWstIbnuDT777RiOLQj4kTn9z0eQmX21Wt3doqODnMDCKu9WiZbWcIGuVF1t7ziCcsuLD+zdpCRuA1prWbzgvbdFe5D32RbEyKEBAY9OcwTNUmsVV4MWVo5H4lf2rXxgAoa2i7/bofj+MfXJRj+79hKCzfAuUAQJBAKcpHxC3EqCpFgvUp8Y+o6RoVoZUdk5fVBoqXJOqLfR3WUe9wHXdf5ma0yARwZW8H4EDeHFYK86tbTR9yT2VilsCQQCR2oWarNCEP5LALBrLGQTtzJjn/AR49LuZMixHbHyUWaAVB0QiRI5NyqWOrg0T2zbf6av8u6qSxYRNJfWVMnYBAkAYs+4AVV2uWm6Enw+QL2+Ve0nWHiNBn7rZBwuZUtvptb1+6Z7IjEwwEf5DTfxyuVNaiH3DGkmXCHAMAs67iB7jAkBEEUBSzB3P6j9ZcfvbFsUHGXmuoh2QOV6ngxPJDWcicKVVyvtQJsBmq5ESAkZWIvL8EnhSTV6wen9g5bszssYBAkAd1I/4Plx7t7LK/irRAaZTgfgegDDP0LwSsi4ai0bVKj+u0068Au3OHL6S7bnOxEgBkbudVbGMkGGeS5lcuAlu",
                "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgF88+tpIpZqqk7Fto7hz3ww7uKiK4OJcNFiWA" +
                        "+t81M8xDzAEOhKMOprNdgFMPcPqNoJHiSM2xRbSz9eG3m1Knm9IMDeJH6iWMak4KBTtn6geaU9qdIJtmb" +
                        "+6ekEX9UQdVeKlFMoUQl2CzjMcq2l/ot0bc+JEkPsXUWfQ4P8oIXxbAgMBAAE=",
                900L, jwtRepo);
    }

//    @Test
//    void resolveClaims_ValidToken_ShouldReturnClaims() throws Exception {
//        String validToken = jwtProvider.generateToken("testUser");
//        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
//
//        Claims claims = jwtProvider.resolveClaims(request);
//
//        assertNotNull(claims);
//        assertEquals("testUser", claims.getSubject());
//    }

    @Test
    void resolveClaims_InvalidToken_ShouldThrowException() {
        String invalidToken = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

        assertThrows(Exception.class, () -> jwtProvider.resolveClaims(request));
    }

    @Test
    void invalidateToken_ShouldAddTokenToBlacklist() {
        String tokenToInvalidate = "invalidToken";

        when(jwtRepo.existsByToken(tokenToInvalidate)).thenReturn(true);

        jwtProvider.invalidateToken(tokenToInvalidate);

        assertTrue(jwtProvider.isTokenBlacklisted(tokenToInvalidate));
    }
}
