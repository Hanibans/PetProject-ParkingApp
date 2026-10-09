package entities;

import java.util.ArrayList;
import java.util.List;

public class Zone {

    private String name;
    private int zoneId;
    private String color;
    private double pricePerHour;
    private int totalSpots;
    private List<Parking> spots;


    public Zone(int zoneId, String name, String color, double pricePerHour, int totalCapacity) {
        this.zoneId = zoneId;
        this.name = name;
        this.color = color;
        this.pricePerHour = pricePerHour;
        this.totalSpots = totalCapacity;
    }


    public int getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(int totalSpots) {
        this.totalSpots = totalSpots;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
