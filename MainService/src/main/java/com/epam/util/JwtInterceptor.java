package com.epam.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNullApi;

import javax.validation.constraints.NotNull;
import java.io.IOException;

public class JwtInterceptor implements ClientHttpRequestInterceptor {

    private final JwtProvider jwtProvider;

    public JwtInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public ClientHttpResponse intercept(org.springframework.http.HttpRequest request,
                                        byte[] body,
                                         ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String token = null; // Replace with your actual username
        try {
            token = jwtProvider.generateToken("your-username");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        headers.add("Authorization", "Bearer " + token);

        return execution.execute(request, body);
    }
}