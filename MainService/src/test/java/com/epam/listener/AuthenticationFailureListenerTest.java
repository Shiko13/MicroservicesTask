package com.epam.listener;

import com.epam.service.LoginAttemptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFailureListenerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationFailureListener failureListener;

    @Test
    void onApplicationEvent_LoginFailureWithoutXForwardedFor_LoginFailedWithRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        Authentication authentication = mock(Authentication.class);
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(authentication,
                new AuthenticationCredentialsNotFoundException("Credentials not found"));

        failureListener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed("127.0.0.1");
    }

    @Test
    void onApplicationEvent_LoginFailureWithXForwardedFor_LoginFailedWithFirstIPAddress() {
        String xForwardedForHeader = "192.168.0.1, 10.0.0.1";
        when(request.getHeader("X-Forwarded-For")).thenReturn(xForwardedForHeader);
        Authentication authentication = mock(Authentication.class);
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(authentication,
                new AuthenticationCredentialsNotFoundException("Credentials not found"));
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        failureListener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed("192.168.0.1");
    }
}