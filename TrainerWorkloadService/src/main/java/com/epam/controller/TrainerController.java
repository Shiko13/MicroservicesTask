package com.epam.controller;

import com.epam.model.dto.TrainerDto;
import com.epam.service.AuthenticationService;
import com.epam.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trainer-another")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    private final AuthenticationService authenticationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody TrainerDto trainerDto, @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);

        authenticationService.verifyToken(token);
        trainerService.save(trainerDto);
    }

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
