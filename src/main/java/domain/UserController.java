package domain;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

public class UserController {
    static UserService userService = new UserService();

    public static void setRoutes(JavalinConfig config) {
        config.routes.get("/", ctx -> ctx.redirect("/login"));
        config.routes.post("/login", ctx -> login(ctx));
        config.routes.get("/login", ctx -> ctx.redirect("/login.html"));
        config.routes.post("/opretkonto", ctx -> createUser(ctx));
        config.routes.get("/opretkonto", ctx -> ctx.redirect("/opretKonto.html"));
        config.routes.get("/logout", ctx -> ctx.redirect("/login"));
        config.routes.get("/support", ctx -> ctx.redirect("/support.html"));
    }

    public static void login(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        User user = userService.login(email, password);
        if (user != null) {
            ctx.redirect("/map.html");
        } else {
            ctx.status(400);
            ctx.result("Forkert email eller adgangskode");
        }
    }

    public static void createUser(Context ctx) {
        String email = ctx.formParam("email");
        String phonenumber = ctx.formParam("phonenumber");
        String password = ctx.formParam("password");

        if (!validateUser(ctx, email, phonenumber, password)){
            return;
        }


        User user = userService.createUser(email, phonenumber, password);
        if (user != null) {
            ctx.redirect("/map.html");
        } else {
            ctx.status(400);
            ctx.result("Der findes allerede en konto med denne email eller dette telefonnummer.");
        }
    }

    private static boolean validateUser(Context ctx, String email, String phonenumber, String password) {
        if (email == null || !email.contains("@")) {
            ctx.status(400);
            ctx.result("Indtast en gyldig email.");
            return false;
        }
        if (password != null && password.length() >= 8 && password.length() <= 15) {
            ctx.status(400);
            ctx.result("Adgangskoden skal have mellem 8 og 15 tegn.");
            return false;
        }
        if (phonenumber == null || phonenumber.length() != 8) {
            ctx.status(400);
            ctx.result("Telefonnummer skal indeholde 8 cifre.");
            return false;
        }
        return true;

    }
}