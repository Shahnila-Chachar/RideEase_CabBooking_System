package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Notification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean createNotification(Notification notification) {
        String query = "INSERT INTO NOTIFICATIONS "
                + "(user_id, booking_id, message, notification_type, is_read) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, notification.getUserId());

            if (notification.getBookingId() == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, notification.getBookingId());
            }

            statement.setString(3, notification.getMessage());
            statement.setString(4, notification.getNotificationType());
            statement.setBoolean(5, notification.isRead());

            if (statement.executeUpdate() == 0) return false;

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    notification.setNotificationId(keys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Notification creation failed: " + e.getMessage());
        }
        return false;
    }

    public Notification findById(int notificationId) {
        String query = "SELECT * FROM NOTIFICATIONS WHERE notification_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, notificationId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return mapNotification(result);
            }
        } catch (SQLException e) {
            System.out.println("Notification search failed: " + e.getMessage());
        }
        return null;
    }

    public List<Notification> findByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM NOTIFICATIONS "
                + "WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    notifications.add(mapNotification(result));
                }
            }
        } catch (SQLException e) {
            System.out.println("User notifications search failed: " + e.getMessage());
        }
        return notifications;
    }

    public List<Notification> findUnreadByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM NOTIFICATIONS "
                + "WHERE user_id = ? AND is_read = FALSE "
                + "ORDER BY created_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    notifications.add(mapNotification(result));
                }
            }
        } catch (SQLException e) {
            System.out.println("Unread notifications search failed: " + e.getMessage());
        }
        return notifications;
    }

    public boolean markAsRead(int notificationId) {
        String query = "UPDATE NOTIFICATIONS SET is_read = TRUE "
                + "WHERE notification_id = ? AND is_read = FALSE";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, notificationId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Notification read update failed: " + e.getMessage());
        }
        return false;
    }

    private Notification mapNotification(ResultSet result) throws SQLException {
        Timestamp timestamp = result.getTimestamp("created_at");
        LocalDateTime createdAt = timestamp == null
                ? null : timestamp.toLocalDateTime();

        int bookingIdValue = result.getInt("booking_id");
        Integer bookingId = result.wasNull() ? null : bookingIdValue;

        return new Notification(
                result.getInt("notification_id"),
                result.getInt("user_id"),
                bookingId,
                result.getString("message"),
                result.getString("notification_type"),
                result.getBoolean("is_read"),
                createdAt
        );
    }
}
