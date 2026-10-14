package services;

import controllers.UserController;
import entities.User;
import exceptions.IllegalUserDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.ConnectionPool;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        ConnectionPool connectionPool = null;
        userService = new UserService(connectionPool);
    }

    @Test
    void validateEmailWithoutSign() {
        assertThrows(IllegalUserDataException.class, ()-> userService.validateNewEmail("gabmail.com"));
    }

    @Test
    void validatePasswordWithLessThanEightSigns() {
        assertThrows(IllegalUserDataException.class, ()-> userService.validateNewPassword("ab34"));

    }

    @Test
    void validatePhonenumberWithAlphabet(){
        assertThrows(IllegalUserDataException.class, ()-> userService.validateNewPhoneNumber("abcdefgh"));
    }



}