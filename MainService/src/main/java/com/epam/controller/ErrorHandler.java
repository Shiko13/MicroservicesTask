package com.epam.controller;

import com.epam.error.AccessException;
import com.epam.error.NotFoundException;
import com.epam.error.TooManyAttemptsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAccessException(AccessException e) {
        log.error("401 {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException e) {
        log.error("404 {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadCredentialsException(BadCredentialsException e) {
        log.error("401 {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleTooManyAttemptsException(TooManyAttemptsException e) {
        log.error("403 {}", e.getMessage());
        return e.getMessage();
    }
}
