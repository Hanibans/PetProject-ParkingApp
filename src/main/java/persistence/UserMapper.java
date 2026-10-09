package persistence;

import entities.User;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import exceptions.*;

public class UserMapper {

    ConnectionPool connectionPool;
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public UserMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public User getUserById(int userId) throws DatabaseException, SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                prepareStatement.setInt(1, userId);
                ResultSet resultSet = prepareStatement.executeQuery();

                if (resultSet.next()) {
                    String mail = resultSet.getString("mail");
                    String password = resultSet.getString("password");

                    User user = new User(mail, password);
                    user.setId(resultSet.getInt("user_id"));

                    return user;
                }
            } catch (SQLException e) {
                throw new DatabaseException("Could not get users from the database", e);
            }
        }
        return null;
    }

    public User getUserByMail(String mail) throws DatabaseException {
        String sql = "SELECT user_id, phone_number, mail, password FROM users WHERE mail = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, mail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("mail"),
                            rs.getString("phone_number"),
                            rs.getString("password")
                    );

                    user.setId(rs.getInt("user_id"));

                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Fejl ved hentning af bruger", e);
            throw new DatabaseException("Fejl ved hentning af bruger", e);
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

    public List<User> getAllUsers() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(sql);
                ResultSet resultSet = prepareStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                String mail = resultSet.getString("mail");
                String password = resultSet.getString("password");
                String phonenumber = resultSet.getString("phoneNumber");
                users.add(new User(mail, password, phonenumber));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get users from the database", e);
        }
        return users;
    }


    public boolean userExists(String mail, String telefon) {
        if (mail != null && telefon != null){}
        return false;
    }

}