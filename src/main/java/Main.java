import Controllers.ParkingController;
import Controllers.PaymentController;
import Controllers.UserController;
import Controllers.VehicleController;
import configuration.ThymeleafConfig;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    public static void main(String[] args) {

        var app = Javalin.create(config -> {
            UserController.setRoutes(config);
            VehicleController.setRoutes(config);
            ParkingController.setRoutes(config);
            PaymentController.setRoutes(config);
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));

        }).start(7070);

    }
}