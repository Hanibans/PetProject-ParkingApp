package services;

import entities.User;
import exceptions.DatabaseException;
import exceptions.IllegalUserDataException;
import persistence.*;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private List<User> users;


    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;

    public UserService(UserMapper userMapper, VehicleMapper vehicleMapper) {
        this.userMapper = userMapper;
        this.vehicleMapper = vehicleMapper;
    }

    public User login(String mail, String password) {
        if (mail == null || password == null) {
            return null;
        }
        try {
            User user = userMapper.getUserByMail(mail);
            if (user == null || !user.getPassword().equals(password)) {
                return null;
            }
            for (Car car : vehicleMapper.getCarsByUserId(user.getId())) {
                user.addCar(car);
            }
            return user;
        } catch (DatabaseException e) {
            return null;
        }
    }

    public User createUser(String mail, String telefon, String password) throws IllegalUserDataException {
        validateEmail(mail);
        validatePhoneNumber(telefon);
        validatePassword(password);

        try {
            if (userMapper.userExists(mail, telefon)) {
                throw new IllegalUserDataException(
                        "Der findes allerede en konto med denne email eller dette telefonnummer.");
            }
            return userMapper.createUser(new User(mail, telefon, password));
        } catch (DatabaseException e) {
            throw new IllegalUserDataException("Kunne ikke oprette brugeren. Prøv igen senere.");
        }
    }


    private void validateEmail(String email) throws IllegalUserDataException {
        if (email == null || !email.contains("@")){
            throw new IllegalUserDataException("Indtast en gyldig email.");
        }
        else if (!email.contains("gmail.com") && !email.contains("hotmail.com") && !email.contains("yahoo.com")){
            throw new IllegalUserDataException("Indtast en gyldig email.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) throws IllegalUserDataException {
        if (phoneNumber == null || phoneNumber.length() != 8 || !phoneNumber.matches("\\d{8}")) {
            throw new IllegalUserDataException("Telefonnummer skal bestå af præcis 8 cifre.");
        }
    }

    private void validatePassword(String password) throws IllegalUserDataException {
        if (password == null || password.length() < 8 || password.length() > 15) {
            throw new IllegalUserDataException("Adgangskoden skal have mellem 8 og 15 tegn.");
        }
    }

}
