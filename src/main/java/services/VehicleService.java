package services;

import entities.Car;
import entities.User;

public class VehicleService {

    public void addVehicle(User user, String licensePlate) {
        Car car = new Car(licensePlate);
        user.addCar(car);
    }

    public void removeVehicle(User user, String licensePlate) {
        user.getCars().removeIf(car -> car.getLicensePlate().equals(licensePlate)
        );
    }

    public boolean hasVehicle(User user) {
        return user.hasCar();
    }
}
