package services;

import entities.Car;
import entities.User;

public class VehicleService {

    public void addVehicle(User user, String licensePlate, String carType) {
        Car car = new Car(licensePlate, carType);
        user.addCar(car);
    }

    public void removeVehicle(User user, String licensePlate) {
        user.getCars().removeIf(car -> car.getLicensePlate().equals(licensePlate)
        );
    }

    public boolean hasVehicle(User user) {
        return user.hasCar();
    }

    public boolean licensePlateExists(User user, String licensePlate) {
        for (Car car : user.getCars()) {
            if (car.getLicensePlate().equalsIgnoreCase(licensePlate)) {
                return true;
            }
        }
        return false;
    }
}
