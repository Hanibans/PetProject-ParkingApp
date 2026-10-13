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
import persistence.VehicleMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ParkingController {

    private ConnectionPool connectionPool;
    private ParkingMapper parkingMapper;
    private ParkingService parkingService;
    private VehicleMapper vehicleMapper;

    public ParkingController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.parkingMapper = new ParkingMapper(connectionPool);
        this.parkingService = new ParkingService(connectionPool);
        this.vehicleMapper = new VehicleMapper(connectionPool);
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
        String baseUrl =
                "http://wfs-kbhkort.kk.dk:80/k101/wfs";

        String url = baseUrl
                + "?service=WFS"
                + "&version=2.0.0"
                + "&request=GetFeature"
                + "&typeNames=k101:p_pladser"
                + "&outputFormat=application/json"
                + "&srsName=EPSG:4326";

        // Hent kortets synlige koordinater, hvis de er sendt med
        String west = ctx.queryParam("west");
        String south = ctx.queryParam("south");
        String east = ctx.queryParam("east");
        String north = ctx.queryParam("north");

        // Afgræns WFS-søgningen til det synlige kortområde
        if (west != null && south != null
                && east != null && north != null) {
            try {
                double minLon = Double.parseDouble(west);
                double minLat = Double.parseDouble(south);
                double maxLon = Double.parseDouble(east);
                double maxLat = Double.parseDouble(north);

                if (!Double.isFinite(minLon)
                        || !Double.isFinite(minLat)
                        || !Double.isFinite(maxLon)
                        || !Double.isFinite(maxLat)
                        || minLon < -180 || maxLon > 180
                        || minLat < -90 || maxLat > 90
                        || minLon >= maxLon
                        || minLat >= maxLat) {
                    ctx.status(400);
                    ctx.result("Ugyldige kortkoordinater.");
                    return;
                }

                String bbox = minLon + "," + minLat + ","
                        + maxLon + "," + maxLat
                        + ",urn:ogc:def:crs:OGC:1.3:CRS84";

                url += "&bbox=" + java.net.URLEncoder.encode(
                        bbox,
                        java.nio.charset.StandardCharsets.UTF_8
                );

            } catch (NumberFormatException e) {
                ctx.status(400);
                ctx.result("Ugyldige kortkoordinater.");
                return;
            }
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(
                    "Status fra WFS (parkeringspladser): "
                            + response.statusCode()
            );

            System.out.println(
                    "Antal tegn i svar: " + response.body().length()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                ctx.status(502);
                ctx.result("Kommunens parkeringsdata kunne ikke hentes.");
                return;
            }

            ctx.contentType("application/json");
            ctx.result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Fejl: " + e.getMessage());
        }
    }


    public void startParking(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401);
            ctx.result("Du skal logge ind for at starte parkering");
            return;
        }

        String zone = ctx.formParam("zone");
        String parkingOrder = mapParkingOrder(
                ctx.formParam("parkingOrder")
        );
        String licensePlate = ctx.formParam("licensePlate");

        if (zone == null || zone.isBlank()
                || licensePlate == null || licensePlate.isBlank()) {
            ctx.status(400);
            ctx.result("Betalingszone og nummerplade skal vælges");
            return;
        }

        try {
            int carId = vehicleMapper.getCarIdByUserIdAndLicensePlate(
                    user.getId(),
                    licensePlate
            );

            String street = ctx.formParam("street");

            int parkingId = parkingMapper.startParking(
                    carId,
                    zone,
                    parkingOrder,
                    street
            );

            ctx.status(200);
            ctx.result("Parkering startet. Parkerings-ID: " + parkingId);

        } catch (exceptions.DatabaseException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    }



    private String mapParkingOrder(String parkingOrder) {
        if (parkingOrder == null) {
            return "regular";
        }

        String order = parkingOrder.toLowerCase();

        if (order.contains("elbil") || order.equals("el")) {
            return "el";
        }

        if (order.contains("handicap") || order.equals("disabled")) {
            return "disabled";
        }

        if (order.contains("delebil") || order.equals("sharing")) {
            return "sharing";
        }

        if (order.contains("privat") || order.equals("private")) {
            return "private";
        }

        return "regular";
    }


    public void stopParking(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401);
            ctx.result("Du skal logge ind for at stoppe parkering.");
            return;
        }

        try {
            java.util.Map<String, Object> parking =
                    parkingMapper.getActiveParkingForUser(user.getId());

            if (parking == null) {
                ctx.status(400);
                ctx.result("Du har ingen aktiv parkering.");
                return;
            }

            int parkingId = (int) parking.get("parkingId");

            java.math.BigDecimal totalCost =
                    parkingMapper.stopParking(parkingId, user.getId());

            ctx.json(java.util.Map.of(
                    "message", "Parkeringen er stoppet.",
                    "totalCost", totalCost
            ));

        } catch (exceptions.DatabaseException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    }
}