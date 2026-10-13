package controllers;

import entities.User;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;
import persistence.PaymentMapper;
import services.PaymentService;

import java.util.HashMap;
import java.util.Map;

public class PaymentController {

    private final ConnectionPool connectionPool;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.paymentService = new PaymentService(connectionPool);
        this.paymentMapper = new PaymentMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/payments", ctx -> showPayments(ctx));
        config.routes.post("/payments/pay", ctx -> finishPayment(ctx));
        config.routes.post("/payments/methods/add", ctx -> addPaymentMethod(ctx));
        config.routes.post("/payments/methods/delete", ctx -> deletePaymentMethod(ctx));
        config.routes.get("/payments/methods", ctx -> getPaymentMethods(ctx));
        config.routes.get("/payment-success", ctx -> {
            entities.User user = ctx.sessionAttribute("user");

            if (user == null) {
                ctx.redirect("/login");
                return;
            }

            ctx.render("payment-success.html");
        });
    }

    public void showPayments(Context ctx) {
        entities.User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            Map<String, Object> model = new HashMap<>();

            model.put(
                    "paymentMethods",
                    paymentMapper.getPaymentMethods(user.getId())
            );

            String parkingIdParam = ctx.queryParam("parkingId");
            boolean hasParkingId =
                    parkingIdParam != null
                            && !parkingIdParam.isBlank();

            model.put("hasParkingId", hasParkingId);

            if (hasParkingId) {
                try {
                    model.put(
                            "parkingId",
                            Integer.parseInt(parkingIdParam)
                    );
                } catch (NumberFormatException e) {
                    ctx.status(400);
                    ctx.result("Ugyldigt parkerings-ID.");
                    return;
                }
            }

            ctx.render("payments.html", model);

        } catch (exceptions.DatabaseException e) {
            ctx.status(500);
            ctx.result("Kunne ikke hente betalingsmetoder.");
        }
    }

    public void getPaymentMethods(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401);
            ctx.result("Du skal logge ind.");
            return;
        }

        try {
            java.util.List<java.util.Map<String, Object>> methods =
                    paymentMapper.getPaymentMethods(user.getId());

            ctx.json(methods);

        } catch (Exception e) {
            ctx.status(500);
            ctx.result("Kunne ikke hente betalingskortene.");
        }
    }

    public void addPaymentMethod(Context ctx) {
        entities.User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            paymentMapper.addPaymentMethod(
                    user.getId(),
                    ctx.formParam("cardholderName"),
                    ctx.formParam("cardLastFour"),
                    ctx.formParam("cardBrand")
            );

            String parkingId = ctx.formParam("parkingId");

            if (parkingId != null && !parkingId.isBlank()) {
                ctx.redirect("/payments?parkingId=" + parkingId);
            } else {
                ctx.redirect("/payments");
            }

        } catch (exceptions.DatabaseException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    }

    public void deletePaymentMethod(Context ctx) {
        entities.User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            int paymentMethodId = Integer.parseInt(
                    ctx.formParam("paymentMethodId")
            );

            paymentMapper.deletePaymentMethod(
                    paymentMethodId,
                    user.getId()
            );

            ctx.redirect("/payments");

        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.result("Ugyldigt betalingsmetode-ID.");

        } catch (exceptions.DatabaseException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    }

    public void finishPayment(Context ctx) {
        entities.User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401);
            ctx.result("Du skal logge ind for at betale.");
            return;
        }

        try {
            int parkingId = Integer.parseInt(
                    ctx.formParam("parkingId")
            );

            int paymentMethodId = Integer.parseInt(
                    ctx.formParam("paymentMethodId")
            );

            boolean belongsToUser =
                    paymentMapper.paymentMethodBelongsToUser(
                            paymentMethodId,
                            user.getId()
                    );

            if (!belongsToUser) {
                ctx.status(400);
                ctx.result("Vælg et gyldigt betalingskort.");
                return;
            }

            paymentMapper.markPaymentAsPaid(
                    parkingId,
                    user.getId()
            );

            ctx.json(java.util.Map.of(
                    "message", "Betalingen er gennemført."
            ));

        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.result("Ugyldigt parkerings-ID eller betalingskort.");

        } catch (exceptions.DatabaseException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    }
}
