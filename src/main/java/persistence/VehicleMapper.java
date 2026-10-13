package persistence;


import entities.Car;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleMapper {

    ConnectionPool connectionPool;
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public VehicleMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    public List<Car> getCarsByUserId(int userId) throws DatabaseException {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT license_plate, car_type FROM car WHERE user_id = ? AND is_active = TRUE";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cars.add(new Car(rs.getString("license_plate"), rs.getString("car_type")));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Kunne ikke hente biler");
        }
        return cars;
    }

    public List<Car> getAllCars() throws DatabaseException {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT license_plate, car_type FROM car";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cars.add(new Car(rs.getString("license_plate"),
                        rs.getString("car_type")));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Kunne ikke hente biler");
        }
        return cars;
    }


    public boolean licensePlateExists(String licensePlate) throws DatabaseException {
        String sql = "SELECT 1 FROM car WHERE license_plate = ? AND is_active = TRUE";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Fejl ved kontrol af nummerplade");
        }
    }

    public void addCar(int userId, String licensePlate, String carType) throws DatabaseException {
        String sql = """
            UPDATE car
            SET car_type = ?, is_active = TRUE
            WHERE user_id = ? AND license_plate = ? AND is_active = FALSE
            """;

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, carType);
            ps.setInt(2, userId);
            ps.setString(3, licensePlate);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                String insertSql = """
                    INSERT INTO car (license_plate, car_type, user_id)
                    VALUES (?, ?, ?)
                    """;

                try (PreparedStatement insertPs = c.prepareStatement(insertSql)) {
                    insertPs.setString(1, licensePlate);
                    insertPs.setString(2, carType);
                    insertPs.setInt(3, userId);
                    insertPs.executeUpdate();
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Bilen blev ikke gemt");
        }
    }

    public void removeCar(int userId, String licensePlate) throws DatabaseException {
        String sql = """
            UPDATE car
            SET is_active = FALSE
            WHERE user_id = ? AND license_plate = ?
            """;

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, licensePlate);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DatabaseException("Bilen blev ikke fundet.");
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Bilen blev ikke slettet.");
        }
    }

    public int getCarIdByUserIdAndLicensePlate(
            int userId, String licensePlate) throws DatabaseException {

        String sql = """
            SELECT car_id
            FROM car
            WHERE user_id = ? AND license_plate = ?
            """;

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, licensePlate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("car_id");
                }

                throw new DatabaseException(
                        "Bilen tilhører ikke den aktuelle bruger");
            }

        } catch (SQLException e) {
            logger.error("Kunne ikke finde bilens ID", e);
            throw new DatabaseException("Kunne ikke finde bilen");
        }
    }

}
