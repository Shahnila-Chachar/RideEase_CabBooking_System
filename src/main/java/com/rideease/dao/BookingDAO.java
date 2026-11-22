package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Booking;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String query = "INSERT INTO BOOKINGS "
                + "(passenger_id, driver_id, vehicle_id, pickup_location, "
                + "destination, vehicle_type, estimated_fare, final_fare, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, booking.getPassengerId());

            if (booking.getDriverId() == null) statement.setNull(2, Types.INTEGER);
            else statement.setInt(2, booking.getDriverId());

            if (booking.getVehicleId() == null) statement.setNull(3, Types.INTEGER);
            else statement.setInt(3, booking.getVehicleId());

            statement.setString(4, booking.getPickupLocation());
            statement.setString(5, booking.getDestination());
            statement.setString(6, booking.getVehicleType());
            statement.setBigDecimal(7, booking.getEstimatedFare());

            if (booking.getFinalFare() == null) statement.setNull(8, Types.DECIMAL);
            else statement.setBigDecimal(8, booking.getFinalFare());

            statement.setString(9, booking.getStatus());

            if (statement.executeUpdate() == 0) return false;

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    booking.setBookingId(keys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Booking creation failed: " + e.getMessage());
        }
        return false;
    }

    public Booking findById(int bookingId) {
        String query = "SELECT * FROM BOOKINGS WHERE booking_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookingId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return mapBooking(result);
            }
        } catch (SQLException e) {
            System.out.println("Booking search failed: " + e.getMessage());
        }
        return null;
    }

    public List<Booking> findByPassengerId(int passengerId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM BOOKINGS WHERE passenger_id = ? ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, passengerId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) bookings.add(mapBooking(result));
            }
        } catch (SQLException e) {
            System.out.println("Passenger bookings search failed: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> findByDriverId(int driverId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM BOOKINGS WHERE driver_id = ? ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, driverId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) bookings.add(mapBooking(result));
            }
        } catch (SQLException e) {
            System.out.println("Driver bookings search failed: " + e.getMessage());
        }
        return bookings;
    }

    public boolean updateStatus(int bookingId, String status) {
        String query = "UPDATE BOOKINGS SET status = ? WHERE booking_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, bookingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Booking status update failed: " + e.getMessage());
        }
        return false;
    }

    public boolean assignDriver(int bookingId, int driverId, int vehicleId) {
        String query = "UPDATE BOOKINGS SET driver_id = ?, vehicle_id = ?, status = 'ACCEPTED' "
                + "WHERE booking_id = ? AND status = 'PENDING'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, driverId);
            statement.setInt(2, vehicleId);
            statement.setInt(3, bookingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Driver assignment failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateFinalFare(int bookingId, BigDecimal finalFare) {
        String query = "UPDATE BOOKINGS SET final_fare = ? WHERE booking_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, finalFare);
            statement.setInt(2, bookingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Final fare update failed: " + e.getMessage());
        }
        return false;
    }

    private Booking mapBooking(ResultSet result) throws SQLException {
        Timestamp timestamp = result.getTimestamp("created_at");
        Timestamp finalFareTimestamp = null;

        int driverIdValue = result.getInt("driver_id");
        Integer driverId = result.wasNull() ? null : driverIdValue;

        int vehicleIdValue = result.getInt("vehicle_id");
        Integer vehicleId = result.wasNull() ? null : vehicleIdValue;

        BigDecimal finalFare = result.getBigDecimal("final_fare");

        LocalDateTime createdAt = timestamp == null ? null : timestamp.toLocalDateTime();

        return new Booking(
                result.getInt("booking_id"),
                result.getInt("passenger_id"),
                driverId,
                vehicleId,
                result.getString("pickup_location"),
                result.getString("destination"),
                result.getString("vehicle_type"),
                result.getBigDecimal("estimated_fare"),
                finalFare,
                result.getString("status"),
                createdAt
        );
    }
}
