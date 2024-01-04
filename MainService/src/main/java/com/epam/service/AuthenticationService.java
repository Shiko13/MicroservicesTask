package com.epam.service;

import com.epam.model.dto.AuthResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    AuthResponse login(String username, String password) throws Exception;

    void logout(HttpServletRequest request);
}
