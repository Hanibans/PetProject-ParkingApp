package domain;

import io.javalin.http.Context;

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


    public User getUser(String mail, String telefon, String password) {
        if (mail == null && telefon == null && password == null) {
            System.out.println("no user found!");
        }

        for (User us : users) {
            if (us.getMail().equals(mail) && us.getPassword().equals(password)) {
                return us;
            }
        }
        return null;
    }

    public User login(String mail, String password) {
        for (User login : users) {
            if (login.getMail().equals(mail) &&
                    login.getPassword().equals(password)) {
                return login;
            }
        }

        return null;
    }

    public User createUser(String mail, String telefon, String password) {
        User user = new User(mail, telefon, password);
        users.add(user);
        return user;
    }



}
