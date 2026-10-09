package services;

import entities.Parking;
import entities.Zone;
import persistence.ParkingMapper;

import java.util.List;

public class ParkingService {
    private final ParkingMapper parkingMapper;

    public ParkingService(ParkingMapper parkingMapper) {
        this.parkingMapper = parkingMapper;
    }

    public List<Parking> getParkingSpots(Zone zone) {
        return zone.getSpots();
    }

    public int getTotalSpots(Zone zone) {
        return zone.getSpots().size();
    }

    public int getAvailableSpots(Zone zone) {

        int availableSpots = 0;

        for (Parking parking : zone.getSpots()) {

            if (parking.isAvailable()) {
                availableSpots++;
            }
        }

        return availableSpots;
    }

    public Parking findAvailableSpot(Zone zone) {

        for (Parking parking : zone.getSpots()) {

            if (parking.isAvailable()) {
                return parking;
            }
        }

        return null;
    }

    public void takeSpot(Parking parking) {
        parking.takeSpot();
    }

    public void releaseSpot(Parking parking) {
        parking.releaseSpot();
    }
}
