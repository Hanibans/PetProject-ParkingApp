package Controllers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import services.VehicleService;

public class VehicleController {

    private final VehicleService vehicleService;

    public static void setRoutes(JavalinConfig config) {
        config.routes.get("/vehicles", ctx -> showVehicles(ctx));
        config.routes.post("/vehicles/add", ctx -> addVehicle(ctx));
        config.routes.post("/vehicles/remove", ctx -> removeVehicle(ctx));
    }

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public static void showVehicles(Context ctx) {
        // Hent den nuværende bruger
        // Hent brugerens biler
        // Send dem til vehicles.html
    }

    public static void addVehicle(Context ctx) {
        String licensePlate = ctx.formParam("licensePlate");

        // vehicleService.addVehicle(...)
    }

    public static void removeVehicle(Context ctx) {
        String licensePlate = ctx.formParam("licensePlate");

        // vehicleService.removeVehicle(...)
    }
}
