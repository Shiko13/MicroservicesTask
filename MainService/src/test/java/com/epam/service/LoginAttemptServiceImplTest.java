package com.epam.service;

import com.google.common.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LoginAttemptServiceImplTest {

    private LoginAttemptServiceImpl loginAttemptService;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        loginAttemptService = new LoginAttemptServiceImpl(request);
    }

    @Test
    void loginFailed_ShouldIncrementAttempts() {
        String key = "userKey";
        loginAttemptService.loginFailed(key);
        int attempts = getAttemptsFromCache(key);
        assertEquals(1, attempts);
    }

    @Test
    void isBlocked_CacheException_ReturnsFalse() {
        boolean blocked = loginAttemptService.isBlocked();

        assertFalse(blocked);
    }

    @Test
    void isBlocked_WhenBelowMaxAttempts_ShouldReturnFalse() {
        String key = "userKey";
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);
        assertFalse(loginAttemptService.isBlocked());
    }

    private int getAttemptsFromCache(String key) {
        try {
            Cache<String, Integer> attemptsCache = loginAttemptService.getAttemptsCache();
            return attemptsCache.get(key, () -> 0);
        } catch (ExecutionException e) {
            fail("Exception while getting attempts from cache", e);
            return 0;
        }
    }
}
