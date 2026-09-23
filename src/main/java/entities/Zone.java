package entities;

import factories.ParkingFactory;

import java.util.ArrayList;
import java.util.List;

public class Zone {

    private String name;
    private double pricePerHour;
    private List<Parking> spots;

    public Zone(String name, double pricePerHour,
                int totalSpots,
                int disabled,
                int el,
                int regular,
                int sharing,
                int privateArea) {

        this.name = name;
        this.pricePerHour = pricePerHour;
        this.spots = new ArrayList<>();

        for (int i = 0; i < disabled; i++) {
            spots.add(ParkingFactory.createParking(
                    "disabled", "D" + i, this));
        }

        for (int i = 0; i < el; i++) {
            spots.add(ParkingFactory.createParking(
                    "el", "E" + i, this));
        }

        for (int i = 0; i < regular; i++) {
            spots.add(ParkingFactory.createParking(
                    "regular", "R" + i, this));
        }

        for (int i = 0; i < sharing; i++) {
            spots.add(ParkingFactory.createParking(
                    "sharing", "S" + i, this));
        }

        for (int i = 0; i < privateArea; i++) {
            spots.add(ParkingFactory.createParking(
                    "private", "P" + i, this));
        }
    }

    public String getName() {
        return name;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public List<Parking> getSpots() {
        return spots;
    }

    @Override
    public String toString() {
        return name + " - " + pricePerHour + " kr/t";
    }
}
