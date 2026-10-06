import controllers.ParkingController;
import controllers.PaymentController;
import controllers.UserController;
import controllers.VehicleController;
import configuration.ThymeleafConfig;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import persistence.ConnectionPool;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "petproject";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {

        var app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            UserController userController = new UserController(connectionPool);
            ParkingController parkingController = new ParkingController(connectionPool);
            UserController.setRoutes(config);
            VehicleController.setRoutes(config);
            ParkingController.setRoutes(config);
            PaymentController.setRoutes(config);



        }).start(7070);

    }
}