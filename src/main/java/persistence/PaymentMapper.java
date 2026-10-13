package persistence;

import exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentMapper {

    private final ConnectionPool connectionPool;

    public PaymentMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void markPaymentAsPaid(int parkingId, int userId)
            throws DatabaseException {

        String sql =
                "UPDATE payment pay " +
                        "SET payment_status = 'Betalt' " +
                        "FROM parking p " +
                        "JOIN car c ON p.car_id = c.car_id " +
                        "WHERE pay.parking_id = p.parking_id " +
                        "AND p.parking_id = ? " +
                        "AND c.user_id = ? " +
                        "AND pay.payment_status = 'Afventer'";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, parkingId);
            ps.setInt(2, userId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DatabaseException(
                        "Betalingen kunne ikke gennemføres."
                );
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Der opstod en fejl ved opdatering af betalingen."
            );
        }
    }

    public List<Map<String, Object>> getPaymentMethods(int userId)
            throws DatabaseException {

        String sql =
                "SELECT payment_method_id, cardholder_name, " +
                        "card_last_four, card_brand, is_default " +
                        "FROM public.payment_method " +
                        "WHERE user_id = ? " +
                        "ORDER BY is_default DESC, payment_method_id";

        List<Map<String, Object>> methods = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> method = new HashMap<>();

                    method.put("paymentMethodId",
                            rs.getInt("payment_method_id"));
                    method.put("cardholderName",
                            rs.getString("cardholder_name"));
                    method.put("cardLastFour",
                            rs.getString("card_last_four"));
                    method.put("cardBrand",
                            rs.getString("card_brand"));
                    method.put("isDefault",
                            rs.getBoolean("is_default"));

                    methods.add(method);
                }
            }

            return methods;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Kunne ikke hente betalingsmetoder."
            );
        }
    }

    public boolean paymentMethodBelongsToUser(
            int paymentMethodId,
            int userId
    ) throws DatabaseException {

        String sql = """
        SELECT 1
        FROM public.payment_method
        WHERE payment_method_id = ?
          AND user_id = ?
        """;

        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, paymentMethodId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Kunne ikke kontrollere betalingskortet."
            );
        }
    }

    public void addPaymentMethod(
            int userId,
            String cardholderName,
            String cardLastFour,
            String cardBrand
    ) throws DatabaseException {

        if (cardholderName == null || cardholderName.isBlank()
                || cardLastFour == null
                || !cardLastFour.matches("\\d{4}")
                || cardBrand == null || cardBrand.isBlank()) {
            throw new DatabaseException(
                    "Ugyldige oplysninger til det simulerede kort."
            );
        }

        String sql =
                "INSERT INTO public.payment_method " +
                        "(user_id, cardholder_name, card_last_four, " +
                        "card_brand, is_default) " +
                        "VALUES (?, ?, ?, ?, " +
                        "NOT EXISTS (" +
                        "SELECT 1 FROM public.payment_method WHERE user_id = ?" +
                        "))";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, cardholderName.trim());
            ps.setString(3, cardLastFour);
            ps.setString(4, cardBrand.trim());
            ps.setInt(5, userId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Kunne ikke tilføje betalingsmetoden."
            );
        }
    }

    public void deletePaymentMethod(int paymentMethodId, int userId)
            throws DatabaseException {

        String sql =
                "DELETE FROM public.payment_method " +
                        "WHERE payment_method_id = ? AND user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, paymentMethodId);
            ps.setInt(2, userId);

            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted == 0) {
                throw new DatabaseException(
                        "Betalingsmetoden blev ikke fundet."
                );
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Kunne ikke fjerne betalingsmetoden."
            );
        }
    }
}
