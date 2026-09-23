package Controllers;

import io.javalin.http.Context;
import service.PaymentService;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
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
}