package com.rideease.dao;

import com.rideease.database.DBConnection;

import java.math.BigDecimal;
import java.sql.*;

public class PaymentDAO {

    public boolean createPayment(int bookingId, BigDecimal amount,
                                 String paymentMethod) {
        String query = "INSERT INTO PAYMENTS "
                + "(booking_id, amount, payment_method) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookingId);
            statement.setBigDecimal(2, amount);
            statement.setString(3, paymentMethod);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Payment creation failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePaymentStatus(int bookingId, String status) {
        String query = "UPDATE PAYMENTS SET status = ?, "
                + "paid_at = CASE WHEN ? = 'SUCCESSFUL' THEN CURRENT_TIMESTAMP ELSE paid_at END "
                + "WHERE booking_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setString(2, status);
            statement.setInt(3, bookingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Payment status update failed: " + e.getMessage());
        }
        return false;
    }

    public boolean paymentExists(int bookingId) {
        String query = "SELECT payment_id FROM PAYMENTS WHERE booking_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookingId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.out.println("Payment search failed: " + e.getMessage());
        }
        return false;
    }
}
