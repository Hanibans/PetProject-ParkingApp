package services;

import entities.User;

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

    public User createUser(String mail, String telefon, String password) {

        if (mail == null || telefon == null || password == null) {
            return null;
        }

        if (!validatePassword(password)) {
            return null;
        }

        // Tjek om email eller telefonnummer allerede findes
        for (User user : users) {
            if (user.getMail().equals(mail)
                    || user.getPhoneNumber().equals(telefon)) {
                return null;
            }
        }

        User user = new User(mail, telefon, password);
        users.add(user);

        return user;
    }

    public boolean validatePassword(String password) {
        return password != null
                && password.length() >= 8
                && password.length() <= 15;
    }
}