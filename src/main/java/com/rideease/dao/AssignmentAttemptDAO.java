package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.RideAssignmentAttempt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * AssignmentAttemptDAO — persistence for RIDE_ASSIGNMENT_ATTEMPTS.
 *
 * The table has UNIQUE(booking_id, driver_id): a driver is never offered the
 * same booking twice, so recordAttempt uses INSERT ... ON DUPLICATE KEY UPDATE
 * to keep the row idempotent.
 */
public class AssignmentAttemptDAO {

    /** insert (or reset) the attempt row; returns attempt_id, or -1 on failure */
    public int recordAttempt(int bookingId, int driverId) {
        String sql = "INSERT INTO RIDE_ASSIGNMENT_ATTEMPTS (booking_id, driver_id, attempt_status) "
                + "VALUES (?, ?, 'PENDING') "
                + "ON DUPLICATE KEY UPDATE attempt_status = 'PENDING', responded_at = NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, driverId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return findId(bookingId, driverId);   // row already existed: look it up
        } catch (Exception e) {
            System.out.println("Attempt logging failed (assignment continues): " + e.getMessage());
            return -1;
        }
    }

    /** status must be ACCEPTED, REJECTED or EXPIRED; returns true on success */
    public boolean recordResponse(int bookingId, int driverId, String status) {
        if (!"ACCEPTED".equals(status) && !"REJECTED".equals(status) && !"EXPIRED".equals(status)) {
            return false;                        // guard before any SQL
        }
        String sql = "UPDATE RIDE_ASSIGNMENT_ATTEMPTS "
                + "SET attempt_status = ?, responded_at = CURRENT_TIMESTAMP "
                + "WHERE booking_id = ? AND driver_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, bookingId);
            statement.setInt(3, driverId);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Attempt response logging failed (assignment continues): " + e.getMessage());
            return false;
        }
    }

    /** all attempts of one booking, in offer order (for history / admin views) */
    public List<RideAssignmentAttempt> findByBookingId(int bookingId) {
        List<RideAssignmentAttempt> attempts = new ArrayList<>();
        String sql = "SELECT attempt_id, booking_id, driver_id, attempt_status, attempted_at, responded_at "
                + "FROM RIDE_ASSIGNMENT_ATTEMPTS WHERE booking_id = ? ORDER BY attempt_id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    attempts.add(new RideAssignmentAttempt(
                            rs.getInt("attempt_id"),
                            rs.getInt("booking_id"),
                            rs.getInt("driver_id"),
                            rs.getString("attempt_status"),
                            rs.getTimestamp("attempted_at") == null ? null : rs.getTimestamp("attempted_at").toLocalDateTime(),
                            rs.getTimestamp("responded_at") == null ? null : rs.getTimestamp("responded_at").toLocalDateTime()));
                }
            }
        } catch (Exception e) {
            System.out.println("Attempt lookup failed: " + e.getMessage());
        }
        return attempts;
    }

    private int findId(int bookingId, int driverId) {
        String sql = "SELECT attempt_id FROM RIDE_ASSIGNMENT_ATTEMPTS WHERE booking_id = ? AND driver_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, driverId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("attempt_id");
                }
            }
        } catch (Exception e) {
            System.out.println("Attempt id lookup failed: " + e.getMessage());
        }
        return -1;
    }
}
