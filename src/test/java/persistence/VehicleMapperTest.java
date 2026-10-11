package persistence;

import entities.Car;
import exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "petproject_test";

    private static ConnectionPool connectionPool;
    private static VehicleMapper vehicleMapper;

    @BeforeAll
    static void setUpClass() {
        connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
        vehicleMapper = new VehicleMapper(connectionPool);
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            // Empties all tables, resets IDs to 1, and follows foreign keys
            stmt.execute("""
                TRUNCATE TABLE car, users, zone_parking_spot_type, parking_spot_type, zone
                RESTART IDENTITY CASCADE
                """);

            stmt.execute("""
                INSERT INTO zone (name, color, price_per_hour, total_capacity) VALUES
                    ('Zone A', 'red',   30.00, 50),
                    ('Zone B', 'blue',  20.00, 80),
                    ('Zone C', 'green', 10.00, 120)
                """);

            stmt.execute("""
                INSERT INTO parking_spot_type (name) VALUES
                    ('regular'), ('disabled'), ('el'), ('sharing'), ('private')
                """);

            stmt.execute("""
                INSERT INTO zone_parking_spot_type (zone_id, parking_spot_type_id, available_spots) VALUES
                    (1, 1, 35),  (1, 2, 5), (1, 3, 10), (1, 4, 0), (1, 5, 0),
                    (2, 1, 60),  (2, 2, 5), (2, 3, 10), (2, 4, 5), (2, 5, 0),
                    (3, 1, 100), (3, 2, 8), (3, 3, 12), (3, 4, 0), (3, 5, 0)
                """);

            stmt.execute("""
                INSERT INTO users (phone_number, mail, password) VALUES
                    ('12345678', 'test@gmail.com', 'test1234'),
                    ('87654321', 'anna@gmail.com', 'anna1234')
                """);

            stmt.execute("""
                INSERT INTO car (license_plate, car_type, user_id) VALUES
                    ('AB12345', 'Almindelig', 1),
                    ('CD67890', 'Elbil',      2)
                """);

        } catch (SQLException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @Test
    void getAllCars() throws DatabaseException {
        List<Car> cars = vehicleMapper.getAllCars();

        assertEquals(2, cars.size());
        assertEquals("AB12345", cars.get(0).getLicensePlate());
    }

    @Test
    void getCarsByUserId() throws DatabaseException {
        List<Car> cars = vehicleMapper.getCarsByUserId(1);

        assertEquals(1, cars.size());
        assertEquals("AB12345", cars.get(0).getLicensePlate());
    }

    @Test
    void licensePlateExists() throws DatabaseException {
        assertTrue(vehicleMapper.licensePlateExists("AB12345"));
        assertFalse(vehicleMapper.licensePlateExists("ZZ99999"));
    }

    @Test
    void addCar() throws DatabaseException {
        vehicleMapper.addCar(1, "XY11111", "Elbil");

        assertTrue(vehicleMapper.licensePlateExists("XY11111"));
        assertEquals(2, vehicleMapper.getCarsByUserId(1).size());
    }

    @Test
    void removeCar() throws DatabaseException {
        vehicleMapper.removeCar(1, "AB12345");

        assertFalse(vehicleMapper.licensePlateExists("AB12345"));
        assertEquals(0, vehicleMapper.getCarsByUserId(1).size());
    }
}