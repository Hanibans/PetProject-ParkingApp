package Controllers;

import io.javalin.http.Context;
import service.VehicleService;

public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public void showVehicles(Context ctx) {
        // Hent den nuværende bruger
        // Hent brugerens biler
        // Send dem til vehicles.html
    }

    public void addVehicle(Context ctx) {
        String licensePlate = ctx.formParam("licensePlate");

        // vehicleService.addVehicle(...)
    }

    public void removeVehicle(Context ctx) {
        String licensePlate = ctx.formParam("licensePlate");

        // vehicleService.removeVehicle(...)
    }
}