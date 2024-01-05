package com.epam.util;

import com.epam.repo.JWTRepo;
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

    @Mock
    private JWTRepo jwtRepo;

    @BeforeEach
    public void setUp() {
        jwtProvider = new JwtProvider(
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDF/INrt4zczs" +
                        "+5bpi2yPU7wUWMUMU7JdcRyxr9J8XP707P0JOGFzpknaUdo3x41D4phB" +
                        "/4m99c3G3ynJw1byBKb7qdZkKI6tuayYfwjpC" +
                        "/xIhxWRXEyQTW86UtGaKDZP3ES8lxffVYORfYcw1mdmiQgneTYQljtK5reBjx1Zj2FZCxSeItA7pNgK9gxM60uwNZb31L0L73BqvOYkhka2Ski8zh7W+XQSHiQFB5Bw6flMwWbDWGcETJOUZQ/KP0I86vXpZsxe4Zj+pPwKAWDcKSi1HHQMvGUzsnG/QmsGvBPVzXNGb2QgasUVNEv/OZvNwpLr+xavvq5LGX39AX5Ln5AgMBAAECggEBAJWNzjQy/YvaCR14hpEkMeLGduOsK6AnUFOXibaLMaiFi5H9a0KSD0DruEswMJkRMjeoH4JrNAlwVPk8Bd9zJwAzb9Lv6PiF+AbvjulOt9az/aEpewEgG7DHcRElWRZJ1ktOOEerrwXKctHE7u1kKk5mzy6b0TzowgxaNvWOtLHFeijZh5UdJByLJDa2M/ROrZypKytWJ91J3CmNqQXl4GPYqSCb1Zh/xWP3GxJQuJ9DqNC8DDTDuVBdKatDbtscNRe4s1INHiHBvSGgolrbpQLFJJosVbbAMwf3w9+qPb+vJlYB5ODCuzGc7mQwbmGZpFqMJW+YKlejtm2SYEeBhuUCgYEA6QLE13cHg0XCy1Gk+BKitDBmPZddwGdQtG5oH8Uv6ktuVi9hWE0DHY9XHCLNEKmAmA1Ytsr20/2CnFumFi7+wGDk8G2g124x0E7Sw1oJB956R8t/rPAi7LvrPUYg/5qy89ywaOIXMppAQKkv+nslT62U+9Px26pidYj++hPOPgsCgYEA2YUe50D7sllvW2/WzVvMtO7N76WhzZEyROeNIvY6VWa/AGesd35B5VblN55MbXu6HDxO0b3GosO6C7JoI/GnJDOl6eP7xmHQLG08YbrYOLUuyCMPmrEHaJGOcsE7iH/aV1QW//PZbxyO8yvdkbIIcPyRUhRaGA6AxACPa+YDXosCgYBcmqscbsPfmhQGthX4OX/MWQfpJoUt+GGMs+xR5A5c8UmSmxX2//RjBRq7peEV5CelRnihfRyHt9L2zT2+x0KbtmaGXytzMuo9PXZqCRhZtkOm0OuXEYDv0Od23K40p4jDfkvmAyTxCXjLufHFfLehaP0y8FLI2Hh0+dNMFB79bQKBgF+O+tePFcPxRFmAFXacKJ8n9fKc6is76UCaFMfSMihIfgp4gSzxm5NcDFDVyy+h1k4HNH0yCOyCXd47n0KDK/Fcezv4OyiY2fJaftdUVR78gbPMq1NDCEUpBNJnD3dU65/Hwl7pSpFkk2xCzhDujITOgCz14eqFnI3+zQMSxlInAoGBAORxqGyQlyYKkoeOSzQrCqqDEQUraBNlct81+eKn4Vnv+1/pj8gqZACihvJHHkRIYugtVn6DecsPg5D9IxDUdkIzAuokwiwQxNkA9KKkIUoIiPrkxyOVz06OB+SsZ0CUpg7Tl5G92hm1tZ6mUhiB2aFqMcP41T1SjGXgWun0QTQ5",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxfyDa7eM3M7PuW6Ytsj1O8FFjFDFOyXXEcsa/SfFz" +
                        "+9Oz9CThhc6ZJ2lHaN8eNQ+KYQf+JvfXNxt8pycNW8gSm" +
                        "+6nWZCiOrbmsmH8I6Qv8SIcVkVxMkE1vOlLRmig2T9xEvJcX31WDkX2HMNZnZokIJ3k2EJY7Sua3gY8dWY9hWQsUniLQO6TYCvYMTOtLsDWW99S9C+9warzmJIZGtkpIvM4e1vl0Eh4kBQeQcOn5TMFmw1hnBEyTlGUPyj9CPOr16WbMXuGY/qT8CgFg3CkotRx0DLxlM7Jxv0JrBrwT1c1zRm9kIGrFFTRL/zmbzcKS6/sWr76uSxl9/QF+S5+QIDAQAB",
                900L, jwtRepo);
    }

    @Test
    void resolveClaims_ValidToken_ShouldReturnClaims() throws Exception {
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

        when(jwtRepo.existsByToken(tokenToInvalidate)).thenReturn(true);

        jwtProvider.invalidateToken(tokenToInvalidate);

        assertTrue(jwtProvider.isTokenBlacklisted(tokenToInvalidate));
    }
}
