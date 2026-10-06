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
    private static final Logger logger =
            LoggerFactory.getLogger(UserMapper.class);

    public UserMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public User login (String userName, String password) throws DatabaseException{

        User user = (userName);
        if(user != null && user.getPassword().equals(password)){
            return user;
        }
        else return null;
    }

    public User createUser(User user) throws DatabaseException {
        String query = "INSERT INTO users (phonenumber, mail, password) " +
                " VALUES ?, ?, ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, user.getPhoneNumber());
            stm.setString(2, user.getMail());
            stm.setString(3, user.getPassword());
            stm.executeUpdate();
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt("user_id"));
                } else throw new DatabaseException("Brugeren kunne ikke oprettes");
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Brugeren blev ikke gemt ");
        }
        return user;
    }
}
