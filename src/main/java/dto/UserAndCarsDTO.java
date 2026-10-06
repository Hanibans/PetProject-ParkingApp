package dto;

import entities.Car;
import entities.User;

import java.util.List;

public record UserAndCarsDTO(User user, List<Car> cars) {
}