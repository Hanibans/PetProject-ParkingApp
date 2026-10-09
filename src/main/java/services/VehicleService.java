package services;

import entities.*;
import persistence.*;
import exceptions.*;

public class VehicleService {

    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleMapper vehicleMapper) {
        this.vehicleMapper = vehicleMapper;
    }

    public void addVehicle(User user, String licensePlate, String carType) throws DatabaseException {
        vehicleMapper.addCar(user.getId(), licensePlate, carType);
        user.addCar(new Car(licensePlate, carType));
    }

    public void removeVehicle(User user, String licensePlate) throws DatabaseException {
        vehicleMapper.removeCar(user.getId(), licensePlate);
        user.getCars().removeIf(car -> car.getLicensePlate().equals(licensePlate));
    }

    public boolean hasVehicle(User user) {
        return user.hasCar();
    }

    public boolean licensePlateExists(User user, String licensePlate) throws DatabaseException {
        return vehicleMapper.licensePlateExists(licensePlate);
    }
}