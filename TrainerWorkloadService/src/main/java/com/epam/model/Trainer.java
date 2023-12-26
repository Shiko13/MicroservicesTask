package com.epam.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Trainer {

    private String username;

    private String firstName;

    private String lastName;

    private Boolean isActive;

    private Map<Integer, Map<Integer, Long>> duration;
}
