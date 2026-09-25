package controllers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ParkingController {

    public static void setRoutes(JavalinConfig config) {
        config.routes.get("/parking", ctx -> showParking(ctx));
        config.routes.get("/parking-zones", ctx -> getParkingZones(ctx));
        config.routes.post("/parking/start", ctx -> startParking(ctx));
        config.routes.post("/parking/stop", ctx -> stopParking(ctx));
    }

    public static void showParking(Context ctx) {
        // Hent zone
        // Hent parkeringspladser
        // Send data til map.html
    }

    public static void getParkingZones(Context ctx) {
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
                    "Status fra WFS: " + response.statusCode()
            );

            System.out.println(
                    "Svar fra WFS:"
            );

            System.out.println(
                    response.body()
            );

            ctx.contentType("application/json");
            ctx.result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result(
                    "Fejl: " + e.getMessage()
            );
        }
    }

    public static void startParking(Context ctx) {
        String zone = ctx.formParam("zone");
        System.out.println(
                "Valgt zone: " + zone
        );
        ctx.redirect("/map");
    }

    public static void stopParking(Context ctx) {

        // Stop den aktive parkering

    }
}