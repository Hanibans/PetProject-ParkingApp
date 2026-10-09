package persistence;

import entities.Zone;
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

}
