package controllers;

import entities.Parking;
import entities.User;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import persistence.ConnectionPool;
import persistence.ParkingMapper;
import persistence.PaymentMapper;
import services.ParkingService;
import services.PaymentService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ParkingController {

    private ConnectionPool connectionPool;
    private ParkingMapper parkingMapper;
    private ParkingService parkingService;

    public ParkingController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.parkingMapper = new ParkingMapper(connectionPool);
        this.parkingService = new ParkingService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/parking", ctx -> showParking(ctx));
        config.routes.get("/parking-zones", ctx -> getParkingZones(ctx));
        config.routes.get("/parking-areas", ctx -> getParkingAreas(ctx));
        config.routes.post("/parking/start", ctx -> startParking(ctx));
        config.routes.post("/parking/stop", ctx -> stopParking(ctx));

    }

    public void showParking(Context ctx) {
        // Hent zone
        // Hent parkeringspladser
        // Send data til map.html
        ctx.redirect("/map");
    }

    public void getParkingZones(Context ctx) {
        String url =
                "http://wfs-kbhkort.kk.dk:80/k101/wfs" +
                        "?service=WFS" +
                        "&version=2.0.0" +
                        "&request=GetFeature" +
                        "&typeNames=k101:betalingszone" +
                        "&outputFormat=application/json" +
                        "&srsName=EPSG:4326";

        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    "Status fra WFS (zoner): "
                            + response.statusCode()
            );

            ctx.contentType("application/json");
            ctx.result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Fejl: " + e.getMessage());
        }
    }

    private void getParkingAreas(Context ctx) {
        String url =
                "http://wfs-kbhkort.kk.dk:80/k101/wfs" +
                        "?service=WFS" +
                        "&version=2.0.0" +
                        "&request=GetFeature" +
                        "&typeNames=k101:parkering_areal" +
                        "&outputFormat=application/json" +
                        "&srsName=EPSG:4326";

        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    "Status fra WFS (parkeringsarealer): "
                            + response.statusCode()
            );

            System.out.println(
                    "Antal tegn i svar: "
                            + response.body().length()
            );

            ctx.contentType("application/json");
            ctx.result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Fejl: " + e.getMessage());
        }
    }

    public void startParking(Context ctx) {

        User user =
                ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        if (!user.hasCar()) {
            ctx.status(400);
            ctx.result("Ingen nummerplade registreret");
            return;
        }

        String zone =
                ctx.formParam("zone");

        String parkingAreaId =
                ctx.formParam("parkingAreaId");

        String licensePlate =
                ctx.formParam("licensePlate");

        System.out.println(
                "Valgt zone: " + zone
        );

        System.out.println(
                "Valgt parkeringsareal: "
                        + parkingAreaId
        );

        System.out.println(
                "Valgt nummerplade: "
                        + licensePlate
        );

        ctx.result(
                "Parkering startet for "
                        + licensePlate
        );
    }

    public void stopParking(Context ctx) {

        // Stop den aktive parkering

    }
}