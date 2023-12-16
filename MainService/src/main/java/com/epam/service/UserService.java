package com.epam.service;

import com.epam.model.User;
import com.epam.model.dto.UserActivateDtoInput;
import com.epam.model.dto.UserDtoInput;
import com.epam.model.dto.UserWithPassword;

import java.util.Optional;

public interface UserService {

    UserWithPassword save(UserDtoInput userDtoInput);

    void changePassword(String username, String oldPassword, String newPassword);

    void switchActivate(String username, UserActivateDtoInput userInput);

    Optional<User> findUserByUsername(String username);
}
