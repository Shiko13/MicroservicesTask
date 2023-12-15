package com.epam.controller;

import com.epam.model.dto.AuthResponse;
import com.epam.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Api(tags = "Auth Controller")
public class AuthController {

    private final AuthenticationService authenticationService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/login")
    @ApiOperation(value = "Login page", notes = "After login will be send Bearer token")
    public AuthResponse login(@RequestParam String username, @RequestParam String password) {
        return authenticationService.login(username, password);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/logout")
    @ApiOperation(value = "Logout page", notes = "After logout Bearer token will be add to blacklist")
    public String logout(HttpServletRequest request) {
        authenticationService.logout(request);

        return "Logout successful";
    }

    @GetMapping
    @ApiOperation(value = "Main page", notes = "For ease of testing")
    public String mainPage() {
        return "Main page will be here";
    }
}
