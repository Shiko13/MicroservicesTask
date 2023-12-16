package com.epam.filter;

import com.epam.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void doFilterInternal_ValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        when(jwtProvider.resolveToken(request)).thenReturn("validAccessToken");

        Claims mockClaims = mock(Claims.class);
        when(jwtProvider.resolveClaims(request)).thenReturn(mockClaims);
        when(jwtProvider.validateClaims(mockClaims)).thenReturn(true);
        when(mockClaims.getSubject()).thenReturn("testUser");

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NullAccessToken_ShouldProceedWithoutSettingAuthentication()
            throws ServletException, IOException {
        when(jwtProvider.resolveToken(request)).thenReturn(null);

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ExceptionDuringTokenResolution_ShouldReturnForbidden() throws ServletException, IOException {
        when(jwtProvider.resolveToken(request)).thenThrow(new RuntimeException("Simulated Token Resolution Exception"));

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidClaims_ShouldSetAuthentication() throws ServletException, IOException {
        String validToken = "validToken";
        when(jwtProvider.resolveToken(request)).thenReturn(validToken);

        Claims validClaims = createValidClaims();
        when(jwtProvider.resolveClaims(request)).thenReturn(validClaims);
        when(jwtProvider.validateClaims(validClaims)).thenReturn(true);

        doNothing().when(filterChain).doFilter(request, response);

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NullAccessToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        when(jwtProvider.resolveToken(request)).thenReturn(null);

        doNothing().when(filterChain).doFilter(request, response);

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    private Claims createValidClaims() {
        return Jwts.claims().setSubject("testUser").setExpiration(new Date(System.currentTimeMillis() + 3600000));
    }
}