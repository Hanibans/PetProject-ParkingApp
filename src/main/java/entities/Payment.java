package entities;

import java.time.Duration;
import java.time.LocalDateTime;

public class Payment {

    private User user;
    private Car car;
    private Parking parking;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double amount;

    public Payment(User user, Car car, Parking parking) {
        this.user = user;
        this.car = car;
        this.parking = parking;
    }

    public void startPayment() {
        startTime = LocalDateTime.now();
    }

    public double endPayment() {

        endTime = LocalDateTime.now();

        long totalSeconds =
                Duration.between(startTime, endTime).toSeconds();

        amount = (totalSeconds / 3600.0)
                * parking.getPricePerHour();

        return amount;
    }

    public User getUser() {
        return user;
    }

    public Car getCar() {
        return car;
    }

    public Parking getParking() {
        return parking;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public double getAmount() {
        return amount;
    }
}