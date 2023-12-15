package com.epam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserWithPassword {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String rawPassword;

    private Boolean isActive;

    private Integer postfix;

    private String encodedPassword;
}
