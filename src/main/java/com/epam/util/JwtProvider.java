package com.epam.util;

import com.epam.model.JWT;
import com.epam.repo.JWTRepo;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtProvider {

    private final String secretKey;
    private final Long expiresInS;
    private final JwtParser jwtParser;
    private final JWTRepo jwtRepo;

    public JwtProvider(@Value("${security.jwt.signing-key}") String secretKey,
                       @Value("${security.jwt.access-token.expires-in-s:900}") Long expiresInS, JWTRepo jwtRepo) {
        this.secretKey = secretKey;
        this.expiresInS = expiresInS;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
        this.jwtRepo = jwtRepo;
    }

    public String generateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        var tokenCreateTime = new Date();
        var tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toSeconds(expiresInS));

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
        jwtRepo.save(new JWT(token));
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtRepo.existsByToken(token);
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        return claims.getExpiration().after(new Date());
    }
}

