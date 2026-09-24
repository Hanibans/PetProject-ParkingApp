package services;

import entities.User;
import exceptions.IllegalUserDataException;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private List<User> users;

    public UserService() {
        this.users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User login(String mail, String password) {
        if (mail == null || password == null) {
          return null;
        }

        for (User user : users) {
            if (user.getMail().equals(mail)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }

    public User createUser(String mail, String telefon, String password) throws IllegalUserDataException {

        validateEmail(mail);
        validatePhoneNumber(telefon);
        validatePassword(password);

        // Tjek om email eller telefonnummer allerede findes
        for (User user : users) {
            if (user.getMail().equals(mail) || user.getPhoneNumber().equals(telefon)) {
                throw new IllegalUserDataException(
                        "Der findes allerede en konto med denne email eller dette telefonnummer.");
            }
        }

        User user = new User(mail, telefon, password);
        users.add(user);

        return user;
    }


    private void validateEmail(String email) throws IllegalUserDataException {
        if (email == null || !email.contains("@")) {
            throw new IllegalUserDataException("Indtast en gyldig email.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) throws IllegalUserDataException {
        if (phoneNumber == null || phoneNumber.length() != 8 || phoneNumber.matches("\\d{8}")) {
            throw new IllegalUserDataException("Telefonnummer skal bestå af præcis 8 cifre.");
        }
    }

    private void validatePassword(String password) throws IllegalUserDataException {
        if (password == null || password.length() < 8 || password.length() > 15) {
            throw new IllegalUserDataException("Adgangskoden skal have mellem 8 og 15 tegn.");
        }
    }

}
