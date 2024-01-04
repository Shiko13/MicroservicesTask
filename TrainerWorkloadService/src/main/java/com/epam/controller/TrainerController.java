package com.epam.controller;

import com.epam.model.dto.TrainerDto;
import com.epam.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.HeaderParam;

@RestController
@RequestMapping("trainer-another")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    private RestTemplate restTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody TrainerDto trainerDto, @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        trainerService.save(trainerDto, token);
    }

    private String extractToken(String authorizationHeader) {
        // Extract the token from the Authorization header (Bearer token)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
