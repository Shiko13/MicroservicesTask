package com.epam.controller;

//@ExtendWith(MockitoExtension.class)
//class AuthControllerTest {
//
//    @InjectMocks
//    private UserController userController;
//
//    @Mock
//    private UserService userService;
//
////    @Test
////    void login_shouldOk() {
////        String username = "testUser";
////        String password = "testPassword";
////
////        doNothing().when(userService).login(username, password);
////
////        userController.login(username, password);
////
////        verify(userService).login(username, password);
////    }
////
////    @Test
////    void login_ShouldThrowAccessException() {
////        String username = "testUser";
////        String password = "testPassword";
////
////        doThrow(AccessException.class).when(userService).login(username, password);
////
////        assertThrows(AccessException.class, () -> userController.login(username, password));
////
////        verify(userService).login(username, password);
////    }
//
//    @Test
//    void changePassword_ShouldOk() {
//        String username = "testUser";
//        String oldPassword = "oldPassword";
//        String newPassword = "newPassword";
//
//        doNothing().when(userService).changePassword(username, oldPassword, newPassword);
//
//        userController.changePassword(username, oldPassword, newPassword);
//
//        verify(userService).changePassword(username, oldPassword, newPassword);
//    }
//
//    @Test
//    void changePassword_ShouldThrowAccessException() {
//        String username = "testUser";
//        String oldPassword = "oldPassword";
//        String newPassword = "newPassword";
//
//        doThrow(AccessException.class).when(userService).changePassword(username, oldPassword, newPassword);
//
//        assertThrows(AccessException.class, () -> userController.changePassword(username, oldPassword, newPassword));
//
//        verify(userService).changePassword(username, oldPassword, newPassword);
//    }
//
//    @Test
//    void switchActivate_ShouldOk() {
//        String username = "testUser";
//        String password = "testPassword";
//        UserActivateDtoInput userInput = new UserActivateDtoInput(true);
//
//        doNothing().when(userService).switchActivate(username, password, userInput);
//
//        userController.switchActivate(username, password, userInput);
//
//        verify(userService).switchActivate(username, password, userInput);
//    }
//
//    @Test
//    void switchActivate_ShouldThrowAccessException() {
//        String username = "testUser";
//        String password = "testPassword";
//        UserActivateDtoInput userInput = new UserActivateDtoInput(true);
//
//        doThrow(AccessException.class).when(userService).switchActivate(username, password, userInput);
//
//        assertThrows(AccessException.class, () -> userController.switchActivate(username, password, userInput));
//
//        verify(userService).switchActivate(username, password, userInput);
//    }
//}
