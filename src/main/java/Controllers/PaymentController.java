package Controllers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import services.PaymentService;

public class PaymentController {

    private final PaymentService paymentService;

    public static void setRoutes(JavalinConfig config) {
        config.routes.get("/payments", ctx -> showPayments(ctx));
        config.routes.get("/parkinghistory", ctx -> showParkingHistory(ctx));
    }

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public static void showPayments(Context ctx) {
        // Hent brugerens betalinger
        // Send dem til payments.html
    }

    public void finishPayment(Context ctx) {
        // Afslut betaling
        // Beregn pris
        // Gem betaling
    }

    private static void showParkingHistory(@NotNull Context ctx) {
    }
}
