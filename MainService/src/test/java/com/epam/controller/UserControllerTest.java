package com.epam.controller;

import com.epam.error.AccessException;
import com.epam.model.dto.UserActivateDtoInput;
import com.epam.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void changePassword_ShouldOk() {
        String username = "testUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        doNothing().when(userService).changePassword(username, oldPassword, newPassword);

        userController.changePassword(username, oldPassword, newPassword);

        verify(userService).changePassword(username, oldPassword, newPassword);
    }

    @Test
    void changePassword_ShouldThrowAccessException() {
        String username = "testUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        doThrow(AccessException.class).when(userService).changePassword(username, oldPassword, newPassword);

        assertThrows(AccessException.class, () -> userController.changePassword(username, oldPassword, newPassword));

        verify(userService).changePassword(username, oldPassword, newPassword);
    }

    @Test
    void switchActivate_ShouldOk() {
        String username = "testUser";
        UserActivateDtoInput userInput = new UserActivateDtoInput(true);

        doNothing().when(userService).switchActivate(username, userInput);

        userController.switchActivate(username, userInput);

        verify(userService).switchActivate(username, userInput);
    }

    @Test
    void switchActivate_ShouldThrowAccessException() {
        String username = "testUser";
        UserActivateDtoInput userInput = new UserActivateDtoInput(true);

        doThrow(AccessException.class).when(userService).switchActivate(username, userInput);

        assertThrows(AccessException.class, () -> userController.switchActivate(username, userInput));

        verify(userService).switchActivate(username, userInput);
    }
}
