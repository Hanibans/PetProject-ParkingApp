package controllers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import persistence.ConnectionPool;
import persistence.PaymentMapper;
import persistence.UserMapper;
import services.PaymentService;
import services.UserService;

public class PaymentController {

    private ConnectionPool connectionPool;
    private PaymentService paymentService;
    private PaymentMapper paymentMapper;

    public PaymentController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.paymentService = new PaymentService(connectionPool);
        this.paymentMapper = new PaymentMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/payments", ctx -> showPayments(ctx));
        config.routes.get("/parkinghistory", ctx -> showParkingHistory(ctx));
    }


    public void showPayments(Context ctx) {
        // Hent brugerens betalinger
        // Send dem til payments.html
    }

    public void finishPayment(Context ctx) {
        // Afslut betaling
        // Beregn pris
        // Gem betaling
    }

    private void showParkingHistory(@NotNull Context ctx) {
    }
}
