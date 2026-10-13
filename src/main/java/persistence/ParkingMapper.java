package persistence;

import entities.Zone;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParkingMapper {
    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(ParkingMapper.class);

    public ParkingMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public List<Zone> getAllZones() throws DatabaseException {
        List<Zone> zones = new ArrayList<>();
        String sql = "SELECT zone_id, name, color, price_per_hour, total_capacity FROM zone";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                zones.add(new Zone(
                        rs.getInt("zone_id"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getDouble("price_per_hour"),
                        rs.getInt("total_capacity")));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Kunne ikke hente zoner");
        }
        return zones;
    }



    public int startParking(
            int carId,
            String zoneName,
            String parkingOrder,
            String street
    ) throws DatabaseException {

        String sql =
                "INSERT INTO parking (" +
                        "car_id, zone_id, parking_spot_type_id, parking_address" +
                        ") " +
                        "SELECT ?, z.zone_id, pst.parking_spot_type_id, ? " +
                        "FROM zone z " +
                        "CROSS JOIN parking_spot_type pst " +
                        "WHERE LOWER(z.name) = LOWER(?) " +
                        "AND pst.name = ? " +
                        "RETURNING parking_id";

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, carId);
            ps.setString(2, street);
            ps.setString(3, zoneName);
            ps.setString(4, parkingOrder);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("parking_id");
                }

                throw new DatabaseException(
                        "Kunne ikke finde betalingszonen eller parkeringsordningen"
                );
            }

        } catch (SQLException e) {
            logger.error("Kunne ikke starte parkering", e);
            throw new DatabaseException("Kunne ikke gemme parkeringen");
        }
    }


    public java.util.Map<String, Object> getActiveParkingForUser(int userId)
            throws DatabaseException {

        String sql = """
        SELECT
            p.parking_id,
            c.license_plate,
            z.name AS zone_name,
            z.price_per_hour,
            p.parking_start_time,
            p.parking_address,
            pst.name AS parking_spot_type
        FROM parking p
        JOIN car c ON p.car_id = c.car_id
        JOIN zone z ON p.zone_id = z.zone_id
        JOIN parking_spot_type pst
            ON p.parking_spot_type_id =
               pst.parking_spot_type_id
        WHERE c.user_id = ?
          AND p.parking_end_time IS NULL
        ORDER BY p.parking_start_time DESC
        LIMIT 1
        """;

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.util.Map<String, Object> parking =
                            new java.util.HashMap<>();

                    parking.put("parkingId",
                            rs.getInt("parking_id"));
                    parking.put("licensePlate",
                            rs.getString("license_plate"));
                    parking.put("zoneName",
                            rs.getString("zone_name"));
                    parking.put("address",
                            rs.getString("parking_address"));
                    parking.put("pricePerHour",
                            rs.getBigDecimal("price_per_hour"));
                    parking.put("startTime",
                            rs.getTimestamp("parking_start_time")
                                    .toInstant().toString());
                    parking.put("parkingSpotType",
                            rs.getString("parking_spot_type"));

                    return parking;
                }

                return null;
            }

        } catch (SQLException e) {
            logger.error("Kunne ikke hente aktiv parkering", e);
            throw new DatabaseException(
                    "Kunne ikke hente den aktive parkering");
        }
    }



    public java.math.BigDecimal stopParking(int parkingId, int userId)
            throws DatabaseException {

        String updateSql =
                "UPDATE parking p " +
                        "SET parking_end_time = NOW() " +
                        "FROM car c, zone z " +
                        "WHERE p.car_id = c.car_id " +
                        "AND p.zone_id = z.zone_id " +
                        "AND p.parking_id = ? " +
                        "AND c.user_id = ? " +
                        "AND p.parking_end_time IS NULL " +
                        "RETURNING p.parking_id, " +
                        "EXTRACT(EPOCH FROM " +
                        "(p.parking_end_time - p.parking_start_time)) " +
                        "* z.price_per_hour / 3600.0 AS total_cost";

        String paymentSql =
                "INSERT INTO payment (total_cost, parking_id) " +
                        "VALUES (?, ?)";

        try (Connection c = connectionPool.getConnection()) {

            c.setAutoCommit(false);

            try {
                java.math.BigDecimal totalCost;
                int updatedParkingId;

                // 1. Afslut parkeringen og beregn prisen
                try (PreparedStatement ps =
                             c.prepareStatement(updateSql)) {

                    ps.setInt(1, parkingId);
                    ps.setInt(2, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            c.rollback();

                            throw new DatabaseException(
                                    "Ingen aktiv parkering blev fundet.");
                        }

                        updatedParkingId =
                                rs.getInt("parking_id");

                        totalCost = rs.getBigDecimal("total_cost")
                                .setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                );
                    }
                }

                // 2. Gem betalingen i payment-tabellen
                try (PreparedStatement ps =
                             c.prepareStatement(paymentSql)) {

                    ps.setBigDecimal(1, totalCost);
                    ps.setInt(2, updatedParkingId);

                    ps.executeUpdate();
                }

                // 3. Gem begge ændringer samlet
                c.commit();

                return totalCost;

            } catch (SQLException e) {

                try {
                    c.rollback();
                } catch (SQLException rollbackError) {
                    logger.error(
                            "Kunne ikke rulle ændringer tilbage",
                            rollbackError
                    );
                }

                logger.error(
                        "Kunne ikke stoppe parkering eller gemme betaling",
                        e
                );

                throw new DatabaseException(
                        "Kunne ikke stoppe parkeringen og gemme betalingen.");
            }

        } catch (SQLException e) {

            logger.error(
                    "Databasefejl ved stop af parkering",
                    e
            );

            throw new DatabaseException(
                    "Kunne ikke stoppe parkeringen.");
        }
    }



    public java.util.List<java.util.Map<String, Object>>
    getParkingHistoryForUser(int userId)
            throws DatabaseException {

        String sql =
                "SELECT p.parking_start_time, " +
                        "p.parking_end_time, " +
                        "c.license_plate, " +
                        "p.parking_address, " +
                        "z.name AS zone_name, " +
                        "z.price_per_hour, " +
                        "pst.name AS parking_spot_type, " +
                        "pay.total_cost " +
                        "FROM parking p " +
                        "JOIN car c ON p.car_id = c.car_id " +
                        "JOIN zone z ON p.zone_id = z.zone_id " +
                        "JOIN parking_spot_type pst " +
                        "ON p.parking_spot_type_id = " +
                        "pst.parking_spot_type_id " +
                        "LEFT JOIN payment pay " +
                        "ON p.parking_id = pay.parking_id " +
                        "WHERE c.user_id = ? " +
                        "AND p.parking_end_time IS NOT NULL " +
                        "ORDER BY p.parking_end_time DESC";

        java.util.List<java.util.Map<String, Object>> history =
                new java.util.ArrayList<>();

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> parking =
                            new java.util.HashMap<>();

                    parking.put("licensePlate",
                            rs.getString("license_plate"));
                    parking.put("address",
                            rs.getString("parking_address"));
                    parking.put("zoneName",
                            rs.getString("zone_name"));
                    parking.put("parkingSpotType",
                            rs.getString("parking_spot_type"));
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern(
                                    "d. MMMM yyyy 'kl.' HH:mm:ss",
                                    new Locale("da", "DK")
                            );

                    ZoneId zoneId = ZoneId.of("Europe/Copenhagen");

                    parking.put("startTime",
                            rs.getTimestamp("parking_start_time")
                                    .toInstant()
                                    .atZone(zoneId)
                                    .format(formatter));

                    parking.put("endTime",
                            rs.getTimestamp("parking_end_time")
                                    .toInstant()
                                    .atZone(zoneId)
                                    .format(formatter));
                    parking.put("pricePerHour",
                            rs.getBigDecimal("price_per_hour"));
                    parking.put("totalCost",
                            rs.getBigDecimal("total_cost"));

                    history.add(parking);
                }

                return history;
            }

        } catch (SQLException e) {
            logger.error("Kunne ikke hente parkeringshistorik", e);
            throw new DatabaseException(
                    "Kunne ikke hente parkeringshistorikken.");
        }
    }
}
