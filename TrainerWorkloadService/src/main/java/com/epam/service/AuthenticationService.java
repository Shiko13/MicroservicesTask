package com.epam.service;

public interface AuthenticationService {

    boolean verifyToken(String token);
}
