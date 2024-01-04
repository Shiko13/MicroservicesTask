package com.epam.util;

import com.epam.model.JWT;
import com.epam.repo.JWTRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtProvider {

    private final String privateKey;
    private final String publicKey;
    private final Long expiresInS;
    private final JWTRepo jwtRepo;

    public JwtProvider(@Value("${security.jwt.signing-key}") String privateKey,
                       @Value("${security.jwt.public-key}") String publicKey,
                       @Value("${security.jwt.access-token.expires-in-s:900}") Long expiresInS, JWTRepo jwtRepo) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.expiresInS = expiresInS;
        this.jwtRepo = jwtRepo;
    }

    public String generateToken(String username) throws Exception {
        Claims claims = Jwts.claims().setSubject(username);

        var tokenCreateTime = new Date();
        var tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toSeconds(expiresInS));

        PrivateKey privateKey1 = convertStringToPrivateKey(privateKey);

        return Jwts.builder()
                   .setClaims(claims)
                   .setExpiration(tokenValidity)
                   .signWith(privateKey1, SignatureAlgorithm.RS512)
                   .compact();
    }

    private Claims parseJwtClaims(String token) throws Exception {
        PublicKey publicKey1 = convertStringToPublicKey(publicKey);

//        return Jwts.parser().setSigningKey(publicKey1).parseClaimsJws(token).getBody();

        return Jwts.parserBuilder().setSigningKey(publicKey1).build().parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) throws Exception {
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

    public static PrivateKey convertStringToPrivateKey(String base64EncodedPrivateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64EncodedPrivateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey convertStringToPublicKey(String base64EncodedPublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64EncodedPublicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }
}

