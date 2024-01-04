package com.epam.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${security.jwt.public-key}")
    private String publicStringKey;

    @Override
    public boolean verifyToken(String token) {
        try {
            PublicKey publicKey = convertStringToPublicKey(publicStringKey);

            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static PublicKey convertStringToPublicKey(String base64EncodedPublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64EncodedPublicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }
}
