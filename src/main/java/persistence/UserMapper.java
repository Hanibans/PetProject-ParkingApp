package persistence;

import entities.User;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    ConnectionPool connectionPool;
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public UserMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public User login(String mail, String password) throws DatabaseException {
        User user = getUserByMail(mail);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserByMail(String mail) throws DatabaseException {
        String sql = "SELECT user_id, phone_number, mail, password FROM users WHERE mail = ?";
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, mail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("mail"),
                            rs.getString("phone_number"),
                            rs.getString("password"),
                            rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Fejl ved hentning af bruger");
        }
        return null;
    }

    public User createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (phone_number, mail, password) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getPhoneNumber());
            ps.setString(2, user.getMail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt("user_id"));
                } else {
                    throw new DatabaseException("Brugeren kunne ikke oprettes");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Brugeren blev ikke gemt");
        }
        return user;
    }


}
