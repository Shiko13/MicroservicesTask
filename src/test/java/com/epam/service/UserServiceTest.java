package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.TooManyAttemptsException;
import com.epam.model.User;
import com.epam.model.dto.UserActivateDtoInput;
import com.epam.model.dto.UserDtoInput;
import com.epam.model.dto.UserWithPassword;
import com.epam.repo.UserRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LoginAttemptServiceImpl loginAttemptService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MeterRegistry meterRegistry;

    private UserDtoInput userDtoInput;

    private User savedUser;
    private UserWithPassword userWithPassword;

    @BeforeEach
    void setUp() {
        userDtoInput = createTestUserDtoInput();
        savedUser = createTestUser(userDtoInput);
        userWithPassword = createTestUserWithPassword(savedUser);
    }

    @Test
    void save_shouldCreateAndReturnUserDtoOutput() {
        UserWithPassword result = userService.save(userDtoInput);

        assertNotNull(result);
        assertEquals(userDtoInput.getFirstName(), result.getFirstName());
        assertEquals(userDtoInput.getLastName(), result.getLastName());
        assertEquals(savedUser.getUsername(), result.getUsername());
    }

    @Test
    void createEntireUser_shouldGenerateUniqueUsername() {
        UserWithPassword user = userService.createEntireUser(userDtoInput);

        assertNotNull(user);
        assertTrue(user.getUsername()
                       .startsWith(userDtoInput.getFirstName().toLowerCase() + "." +
                               userDtoInput.getLastName().toLowerCase()));
    }

    @Test
    void createEntireUser_shouldUseBaseUsernameIfUnique() {
        UserWithPassword user = userService.createEntireUser(userDtoInput);

        assertNotNull(user);
        assertEquals(savedUser.getUsername(), user.getUsername());
    }

    @Test
    void isUsernameExistsInDatabase_shouldReturnTrueIfUsernameExists() {
        when(userRepo.existsByUsername(savedUser.getUsername())).thenReturn(true);

        boolean usernameExists = userService.isUsernameExistsInDatabase(savedUser.getUsername());

        assertTrue(usernameExists);
    }

    @Test
    void isUsernameExistsInDatabase_shouldReturnFalseIfUsernameDoesNotExist() {
        when(userRepo.existsByUsername(savedUser.getUsername())).thenReturn(false);

        boolean usernameExists = userService.isUsernameExistsInDatabase(savedUser.getUsername());

        assertFalse(usernameExists);
    }

    @Test
    void changePassword_InvalidOldPassword_ThrowsAccessException() {
        String username = savedUser.getUsername();
        String password = savedUser.getPassword();
        String invalidPassword = "Invalid password";
        User userWithInvalidPassword = createTestUser(userDtoInput);
        userWithInvalidPassword.setPassword(invalidPassword);

        when(userRepo.findByUsername(savedUser.getUsername())).thenReturn(Optional.of(savedUser));

        AccessException exception = assertThrows(AccessException.class,
                () -> userService.changePassword(username, password, invalidPassword));

        verify(userRepo).findByUsername(savedUser.getUsername());
        verify(userRepo, never()).save(any(User.class));

        assertEquals("Old password is incorrect", exception.getMessage());
    }

    @Test
    void changePassword_ValidOldPassword_PasswordUpdated() {
        String username = "john.doe";
        String oldPassword = "validOldPassword";
        String newPassword = "newPassword";
        User user = new User();
        user.setPassword("encodedValidOldPassword");

        when(userRepo.findByUsername(eq(username))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(oldPassword), any())).thenReturn(true);

        userService.changePassword(username, oldPassword, newPassword);

        verify(passwordEncoder).encode(eq(newPassword));
        verify(userRepo).save(user);
    }

    @Test
    void switchActivate_UserExists_UserActivationStatusUpdated() {
        String username = "john.doe";
        UserActivateDtoInput userInput = new UserActivateDtoInput();
        userInput.setIsActive(true);
        User user = new User();

        Mockito.when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        userService.switchActivate(username, userInput);

        verify(userRepo).save(user);
        assertTrue(user.getIsActive());
    }

    @Test
    void findUserByUsername_WithValidUserName_ShouldReturnUser() {
        String validUsername = savedUser.getUsername();
        when(userRepo.findByUsernameAndPostfix(validUsername, 0)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.findUserByUsername(validUsername);

        assertTrue(result.isPresent());
        assertEquals(savedUser, result.get());
    }

    @Test
    void findUserByUsername_WithUsernameWithPrefix_ShouldReturnUser() {
        String usernameWithPrefix = savedUser.getUsername() + "-1";
        User expectedUser = createTestUser(userDtoInput);
        expectedUser.setUsername(usernameWithPrefix);

        when(userRepo.findByUsernameAndPostfix(savedUser.getUsername(), 1)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findUserByUsername(usernameWithPrefix);

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }

    @Test
    void findUserByUsername_WithInvalidUsername_ShouldReturnEmpty() {
        String invalidUsername = "nonexistent.user";

        when(userRepo.findByUsernameAndPostfix(invalidUsername, 0)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByUsername(invalidUsername);

        assertTrue(result.isEmpty());
    }

    @Test
    void createEntireUser_WhenUsernameDoesNotExist_ShouldReturnUserWithZeroPrefix() {
        when(userService.isUsernameExistsInDatabase(savedUser.getUsername())).thenReturn(false);

        UserWithPassword resultUser = userService.createEntireUser(userDtoInput);

        assertNotNull(resultUser);
        assertEquals(savedUser.getUsername(), resultUser.getUsername());
        assertEquals(0, resultUser.getPostfix());
    }

    @Test
    void createEntireUser_WhenUsernameExistsWithPrefix_ShouldReturnUserWithIncrementedPrefix() {
        when(userService.isUsernameExistsInDatabase(savedUser.getUsername())).thenReturn(true);
        when(userRepo.findMaxPostfixByUsername(savedUser.getUsername())).thenReturn(2);

        UserWithPassword resultUser = userService.createEntireUser(userDtoInput);

        assertNotNull(resultUser);
        assertEquals(savedUser.getUsername(), resultUser.getUsername());
        assertEquals(3, resultUser.getPostfix());
    }

    @Test
    void loadUserByUsername_UserExistsAndNotBlocked_UserDetailsReturned() {
        String username = "john.doe";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setIsActive(true);

        when(loginAttemptService.isBlocked()).thenReturn(false);
        when(userRepo.findByUsernameAndPostfix(username, 0)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(username);

        verify(loginAttemptService).isBlocked();
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        String username = "john.doe";

        Mockito.when(loginAttemptService.isBlocked()).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void loadUserByUsername_UserBlocked_ThrowsTooManyAttemptsException() {
        String username = "john.doe";

        Mockito.when(loginAttemptService.isBlocked()).thenReturn(true);

        assertThrows(TooManyAttemptsException.class, () -> userService.loadUserByUsername(username));
    }

    private UserDtoInput createTestUserDtoInput() {
        return UserDtoInput.builder().firstName("John").lastName("Doe").build();
    }

    private User createTestUser(UserDtoInput userDtoInput) {
        return User.builder()
                   .id(1L)
                   .firstName(userDtoInput.getFirstName())
                   .lastName(userDtoInput.getLastName())
                   .username(userDtoInput.getFirstName().toLowerCase() + "." + userDtoInput.getLastName().toLowerCase())
                   .password("password")
                   .postfix(0)
                   .build();
    }

    private UserWithPassword createTestUserWithPassword(User user) {
        return UserWithPassword.builder()
                               .id(user.getId())
                               .firstName(user.getFirstName())
                               .lastName(user.getLastName())
                               .username(user.getUsername())
                               .rawPassword("rawPassword")
                               .encodedPassword(user.getPassword())
                               .postfix(user.getPostfix())
                               .build();
    }
}
