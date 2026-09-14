package domain;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int phoneNumber;
    private String mail;
    //private String password;
    private int licensPlate;
    private List<Car> cars = new ArrayList<>();
    private ArrayList<Car> license = new ArrayList<>();

    public User(int phoneNumber, String mail) {
        this.mail = mail;
        this.phoneNumber = phoneNumber;
        //this.password = password;
    }

    public void addCarLicense(Car car) {
        license.add(car);
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    /*public String getPassword() {
        return password;
    }*/

    public String getMail(){
        return mail;
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
        return "domain.User{" +
                "mail='" + mail + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }
}