package com.epam.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    public static final int MAX_ATTEMPT = 3;
    @Getter
    private final LoadingCache<String, Integer> attemptsCache;
    private final HttpServletRequest request;

    public LoginAttemptServiceImpl(HttpServletRequest request) {
        this.request = request;
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<>() {
            @Override
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void loginFailed(String key) {
        int attempts;

        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            log.error("Error get value by key from attemptsCache", e);
            attempts = 0;
        }

        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            log.error("Error get value by key from attemptsCache", e);
            return false;
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }

        return request.getRemoteAddr();
    }
}
