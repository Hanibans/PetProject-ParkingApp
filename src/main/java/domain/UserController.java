package domain;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;


public class UserController {
    static UserService userService = new UserService();

    public static void setRoutes(JavalinConfig config) {
        config.routes.post("/login", ctx -> login(ctx));
        config.routes.get("/login", ctx -> ctx.redirect("/login.html"));
        config.routes.post("/createuser", ctx -> createUser(ctx));
        config.routes.get("/createuser", ctx -> ctx.redirect("/createUser.html"));

    }

    public static void login(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        User user = userService.login(email, password);
        if (user != null) {
            ctx.redirect("/map.html");
        } else {
            ctx.status(404);
            ctx.result("Brugeren findes ikke");
        }
    }

    public static void createUser(Context ctx) {
        String email = ctx.formParam("email");
        String phonenumber = ctx.formParam("phonenumber");
        String password = ctx.formParam("password");

        User user = userService.createUser(email, phonenumber, password);
        if (user != null) {
            ctx.redirect("/map.html");
        } else {
            ctx.status(404);
            ctx.result("Brugeren kan ikke oprettes");
        }
    }

}