package Controllers;

import io.javalin.http.Context;
import service.ParkingService;

public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    public void showParking(Context ctx) {
        // Hent zone
        // Hent parkeringspladser
        // Send data til map.html
    }

    public void startParking(Context ctx) {
        // Find bruger
        // Find bil
        // Find parkeringsplads
        // Start parkering
    }

    public void stopParking(Context ctx) {
        // Stop den aktive parkering
    }
}