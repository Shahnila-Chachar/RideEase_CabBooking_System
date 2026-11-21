package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Booking;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookingDAOTest {

    public static void main(String[] args) {
        int passengerId = findPassengerId();

        if (passengerId == 0) {
            System.out.println("Test skipped: create a passenger record first.");
            return;
        }

        Booking booking = new Booking(
                0, passengerId, null, null,
                "Sukkur IBA University", "Clock Tower Sukkur",
                "RICKSHAW", new BigDecimal("250.00"),
                null, "PENDING", null
        );

        BookingDAO dao = new BookingDAO();

        System.out.println("===== CREATE BOOKING TEST =====");
        boolean created = dao.createBooking(booking);
        System.out.println("Created: " + created);
        System.out.println("Booking ID: " + booking.getBookingId());

        if (!created) return;

        System.out.println("\n===== FIND BOOKING TEST =====");
        Booking found = dao.findById(booking.getBookingId());
        System.out.println("Found: " + (found != null));

        System.out.println("\n===== FIND PASSENGER BOOKINGS TEST =====");
        System.out.println("Bookings: " + dao.findByPassengerId(passengerId).size());

        System.out.println("\n===== UPDATE STATUS TEST =====");
        System.out.println("Updated: " + dao.updateStatus(booking.getBookingId(), "CANCELLED"));
    }

    private static int findPassengerId() {
        String query = "SELECT passenger_id FROM PASSENGERS LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) return result.getInt(1);
        } catch (Exception e) {
            System.out.println("Passenger lookup failed: " + e.getMessage());
        }
        return 0;
    }
}
