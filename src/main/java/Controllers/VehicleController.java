package Controllers;

import entities.User;
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
        if (licensePlate == null || licensePlate.isBlank()) {
            ctx.status(400);
            ctx.result("Indtast en nummerplade.");
            return;
        }
        if (licensePlate.length() != 7){
            ctx.status(400);
            ctx.result("Nummerplade skal have 7 tegn!");
        }

        vehicleService.addVehicle(user, licensePlate);
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
