package controllers;

import entities.User;
import exceptions.IllegalUserDataException;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.*;
import services.UserService;

public class UserController {

    private ConnectionPool connectionPool;
    private UserService userService;
    private UserMapper userMapper;
    private ParkingMapper parkingMapper;

    public UserController(ConnectionPool connectionPool){
      this.connectionPool = connectionPool;
      this.userService = new UserService(connectionPool);
      this.userMapper = new UserMapper(connectionPool);
        this.parkingMapper = new ParkingMapper(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/", ctx -> ctx.redirect("/login"));
        config.routes.post("/login", ctx -> login(ctx));
        config.routes.get("/login", ctx -> ctx.redirect("/login.html"));
        config.routes.post("/createuser", ctx -> createUser(ctx));
        config.routes.get("/createuser", ctx -> ctx.redirect("/createuser.html"));
        config.routes.get("/logout", ctx -> logout(ctx));
        config.routes.get("/account", ctx -> showAccount(ctx));
        config.routes.get("/support", ctx -> ctx.redirect("/support.html"));
        config.routes.get("/menu", ctx -> ctx.redirect("/menu.html"));
        config.routes.get("/active-parking", ctx -> {
            User user = ctx.sessionAttribute("user");

            if (user == null) {
                ctx.redirect("/login");
                return;
            }

            try {
                java.util.Map<String, Object> parking =
                        parkingMapper.getActiveParkingForUser(user.getId());

                if (parking == null) {
                    ctx.redirect("/map.html");
                    return;
                }

                ctx.render("active-parking.html",
                        java.util.Map.of("parking", parking));

            } catch (exceptions.DatabaseException e) {
                ctx.status(500);
                ctx.result(e.getMessage());
            }
        });

        config.routes.get("/parkinghistory", ctx -> {
            User user = ctx.sessionAttribute("user");

            if (user == null) {
                ctx.redirect("/login");
                return;
            }

            try {
                java.util.List<java.util.Map<String, Object>> history =
                        new ParkingMapper(connectionPool)
                                .getParkingHistoryForUser(user.getId());

                ctx.render("parkinghistory.html",
                        java.util.Map.of("history", history));

            } catch (exceptions.DatabaseException e) {
                ctx.status(500);
                ctx.result(e.getMessage());
            }
        });

    }

    public void login(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        User user = userService.login(email, password);
        if (user != null) {
            ctx.sessionAttribute("user", user);
            ctx.redirect("/map.html");
        } else {
            ctx.status(400);
            ctx.result("Forkert email eller adgangskode");
        }
    }

    public void createUser(Context ctx) {
        String email = ctx.formParam("email");
        String phonenumber = ctx.formParam("phonenumber");
        String password = ctx.formParam("password");

        try {
            User user = userService.createUser(email, phonenumber, password);
            ctx.sessionAttribute("user", user);
            ctx.redirect("/map.html");

        } catch (IllegalUserDataException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }

    }

    public void logout(Context ctx) {
        ctx.sessionAttribute("user", null);
        ctx.redirect("/login");
    }

    public void showAccount(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }
        ctx.render("account.html", java.util.Map.of("user", user));
    }

}
