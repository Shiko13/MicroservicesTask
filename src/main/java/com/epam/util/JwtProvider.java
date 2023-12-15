package com.epam.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtProvider {

    private final String secretKey;
    private final Long expiresInS;
    private final JwtParser jwtParser;

    //TODO Save blackList in database (not relative maybe)?
    //TODO Stop brut force for registration profile
    //TODO Read about different methods of authentications
    private final Set<String> tokenBlacklist = new HashSet<>();

    public JwtProvider(@Value("${security.jwt.signing-key}") String secretKey,
                       @Value("${security.jwt.access-token.expires-in-s:900}") Long expiresInS) {
        this.secretKey = secretKey;
        this.expiresInS = expiresInS;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String generateToken(String username) {
        //TODO LocalDate or Instant with TimeZone
        Claims claims = Jwts.claims().setSubject(username);
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toSeconds(expiresInS));

        return Jwts.builder()
                   .setClaims(claims)
                   .setExpiration(tokenValidity)
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                if (isTokenBlacklisted(token)) {
                    throw new ExpiredJwtException(null, null, "Token has been revoked");
                }
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException e) {
            log.error("Token is expired", e);
            req.setAttribute("expired", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Token is invalid", e);
            req.setAttribute("invalid", e.getMessage());
            throw e;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String tokenHeader = "Authorization";
        String bearerToken = request.getHeader(tokenHeader);
        String tokenPrefix = "Bearer ";

        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }

        return null;
    }

    public void invalidateToken(String token) {
        tokenBlacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        return claims.getExpiration().after(new Date());
    }
}

