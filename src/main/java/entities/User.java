package entities;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String phoneNumber;
    private String mail;
    private String password;
    private List<Car> cars = new ArrayList<>();

    public User(String mail, String phoneNumber, String password) {
        this.mail = mail;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public boolean hasCar() {
        return !cars.isEmpty();
    }

    @Override
    public String toString() {
        return mail + " - " + phoneNumber;
    }
}
