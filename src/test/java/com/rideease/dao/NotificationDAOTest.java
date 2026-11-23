package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NotificationDAOTest {

    public static void main(String[] args) {
        int userId = findUserId();

        if (userId == 0) {
            System.out.println("Test skipped: create a user record first.");
            return;
        }

        Notification notification = new Notification(
                0, userId, null,
                "RideEase test notification",
                "SYSTEM", false, null
        );

        NotificationDAO dao = new NotificationDAO();

        System.out.println("===== CREATE NOTIFICATION TEST =====");
        boolean created = dao.createNotification(notification);
        System.out.println("Created: " + created);

        if (!created) return;

        int notificationId = notification.getNotificationId();
        System.out.println("Notification ID: " + notificationId);

        System.out.println("\n===== FIND BY ID TEST =====");
        Notification found = dao.findById(notificationId);
        System.out.println("Found: " + (found != null));

        System.out.println("\n===== FIND BY USER ID TEST =====");
        System.out.println("Notifications found: "
                + dao.findByUserId(userId).size());

        System.out.println("\n===== FIND UNREAD TEST =====");
        System.out.println("Unread notifications: "
                + dao.findUnreadByUserId(userId).size());

        System.out.println("\n===== MARK AS READ TEST =====");
        boolean updated = dao.markAsRead(notificationId);
        System.out.println("Marked as read: " + updated);

        System.out.println("\n===== VERIFY READ STATUS TEST =====");
        Notification updatedNotification = dao.findById(notificationId);
        System.out.println("Is read: "
                + (updatedNotification != null && updatedNotification.isRead()));
    }

    private static int findUserId() {
        String query = "SELECT user_id FROM USERS LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) return result.getInt(1);

        } catch (Exception e) {
            System.out.println("User lookup failed: " + e.getMessage());
        }
        return 0;
    }
}
