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
        String sql = "SELECT license_plate, car_type FROM car WHERE user_id = ?";
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

    public boolean licensePlateExists(String licensePlate) throws DatabaseException {
        String sql = "SELECT 1 FROM car WHERE license_plate = ?";
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
        String sql = "INSERT INTO car (license_plate, car_type, user_id) VALUES (?, ?, ?)";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
            ps.setString(2, carType);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Bilen blev ikke gemt");
        }
    }

    public void removeCar(int userId, String licensePlate) throws DatabaseException {
        String sql = "DELETE FROM car WHERE user_id = ? AND license_plate = ?";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, licensePlate);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Bilen blev ikke slettet.");
        }
    }
}
