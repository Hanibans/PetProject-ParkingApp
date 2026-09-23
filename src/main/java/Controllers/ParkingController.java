package Controllers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import services.ParkingService;

public class ParkingController {

    private final ParkingService parkingService;

    public static void setRoutes(JavalinConfig config) {
        config.routes.get("/parking", ctx -> showParking(ctx));
        config.routes.post("/parking/start", ctx -> startParking(ctx));
        config.routes.post("/parking/stop", ctx -> stopParking(ctx));
    }

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    public static void showParking(Context ctx) {
        // Hent zone
        // Hent parkeringspladser
        // Send data til map.html
    }

    public static void startParking(Context ctx) {
        // Find bruger
        // Find bil
        // Find parkeringsplads
        // Start parkering
    }

    public static void stopParking(Context ctx) {
        // Stop den aktive parkering
    }
}