package com.epam.service;

import com.epam.model.dto.TrainerDto;

public interface TrainerService {

    void save(TrainerDto trainerDto, String token);
}
