package com.epam.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TrainerWorkloadDto {

    private String username;

    private String firstName;

    private String lastName;

    private Boolean isActive;

    private LocalDate date;

    private Long duration;

    private ActionType actionType;
}
