package domain;

import java.util.List;

public class UserService {
    private List<User> users;

    public UserService() {
        this.users = UserFactory.createUsers();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        if (user == null) {
            System.out.println("no users added");
        }

        users.add(user);
        System.out.println(user + "added!");

    }

    public User getUser(String mail, String telefon, String password) {
        if (mail == null && telefon == null && password == null) {
            System.out.println("no user found!");
        }

        for (User us : users) {
            if (us.getMail().equals(mail) && us.getPassword.equals(password)) {
                return us;
            }
        }
        return null;
    }

    public User login(String mail, String telefon, String password) {
        if (mail == null && telefon == null && password == null) {
            System.out.println("no login!");
            return null;
        }

        for (User login : users) {
            if (login.getMail().equals(mail) &&
                    login.getPassword().equals(password)) {
                System.out.println("Login succesful!");
                return login;
            }
        }

        return null;
    }

    public User createUser(String mail, String telefon, String password) {
        if (mail == null && telefon == null && password == null) {
            System.out.println("no user created!");
        }

        for (User us : users) {
            if (us.getMail().equals(mail) && us.getPassword.equals(password)) {
                return null;
            }
        }

        /*User user = new User(mail, telefon, password);
        users.add(user);


    }*/
        return user;
    }

}
