package controllers;

import entities.User;
import entities.Car;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import services.VehicleService;

public class VehicleController {

    private static VehicleService vehicleService = new VehicleService();

    public static void setRoutes(JavalinConfig config) {
        config.routes.get("/vehicles", ctx -> showVehicles(ctx));
        config.routes.post("/vehicles/add", ctx -> addVehicle(ctx));
        config.routes.post("/vehicles/remove", ctx -> removeVehicle(ctx));
    }


    public static void showVehicles(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }
        ctx.render("vehicles.html",
                java.util.Map.of("cars", user.getCars()));
    }

    public static void addVehicle(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        String licensePlate = ctx.formParam("licensePlate");
        String carType = ctx.formParam("carType");

        if (carType == null || carType.isBlank()) {
            ctx.status(400);
            ctx.result("Vælg en biltype.");
            return;
        }

        if (licensePlate == null || licensePlate.isBlank()) {
            ctx.status(400);
            ctx.result("Indtast en nummerplade.");
            return;
        }

        // Fjern mellemrum og gør bogstaver store
        licensePlate = licensePlate
                .replaceAll("\\s+", "")
                .toUpperCase();

        // Skal være præcis 2 bogstaver + 5 tal
        if (!licensePlate.matches("[A-Z]{2}\\d{5}")) {
            ctx.status(400);
            ctx.result("Nummerpladen skal have 2 bogstaver og 5 tal. Fx AB12345.");
            return;
        }

        // Tjek om nummerpladen allerede findes
        if (vehicleService.licensePlateExists(user, licensePlate)) {
            ctx.status(400);
            ctx.result("Denne nummerplade er allerede registreret.");
            return;
        }

        vehicleService.addVehicle(user, licensePlate, carType);
        ctx.redirect("/vehicles");
    }

    public static void removeVehicle(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        String licensePlate = ctx.formParam("licensePlate");
        vehicleService.removeVehicle(user, licensePlate);
        ctx.redirect("/vehicles");
    }
}
