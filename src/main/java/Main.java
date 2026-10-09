import controllers.ParkingController;
import controllers.PaymentController;
import controllers.UserController;
import controllers.VehicleController;
import configuration.ThymeleafConfig;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import persistence.*;
import services.ParkingService;
import services.PaymentService;
import services.UserService;
import services.VehicleService;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "petproject";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {

        public static void main(String[] args) {

            // Mappere (får poolen)
            UserMapper userMapper = new UserMapper(connectionPool);
            VehicleMapper vehicleMapper = new VehicleMapper(connectionPool);
            ParkingMapper parkingMapper = new ParkingMapper(connectionPool);
            PaymentMapper paymentMapper = new PaymentMapper(connectionPool);

            // Services (får mapperne)
            UserService userService = new UserService(userMapper, vehicleMapper);
            VehicleService vehicleService = new VehicleService(vehicleMapper);
            ParkingService parkingService = new ParkingService(parkingMapper);
            PaymentService paymentService = new PaymentService(paymentMapper);

            // Controllere (får services)
            UserController userController = new UserController(userService);
            VehicleController vehicleController = new VehicleController(vehicleService);
            ParkingController parkingController = new ParkingController(parkingService);
            PaymentController paymentController = new PaymentController(paymentService);

            Javalin.create(config -> {
                config.staticFiles.add("/public");
                config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));

                userController.setRoutes(config);
                vehicleController.setRoutes(config);
                parkingController.setRoutes(config);
                paymentController.setRoutes(config);
            }).start(7070);
        }

    }
}