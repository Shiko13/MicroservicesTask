package com.epam.service;

import com.epam.model.dto.AuthResponse;
import com.epam.util.JwtProvider;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    @Counted("login_attempts")
    public AuthResponse login(String username, String password) {
        var authenticate =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String token = jwtProvider.generateToken(authenticate.getName());

        return new AuthResponse(token);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        jwtProvider.invalidateToken(token);
    }
}
