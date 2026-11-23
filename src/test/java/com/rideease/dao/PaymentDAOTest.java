
package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Booking;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaymentDAOTest {

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

        BookingDAO bookingDAO = new BookingDAO();

        System.out.println("===== CREATE BOOKING TEST =====");
        boolean bookingCreated = bookingDAO.createBooking(booking);
        System.out.println("Created: " + bookingCreated);

        if (!bookingCreated) return;

        int bookingId = booking.getBookingId();
        System.out.println("Booking ID: " + bookingId);

        System.out.println("\n===== COMPLETE BOOKING TEST =====");
        boolean completed = bookingDAO.updateStatus(bookingId, "COMPLETED");
        System.out.println("Completed: " + completed);

        if (!completed) return;

        PaymentDAO paymentDAO = new PaymentDAO();
        BigDecimal amount = new BigDecimal("250.00");

        System.out.println("\n===== CREATE PAYMENT TEST =====");
        boolean created = paymentDAO.createPayment(
                bookingId, amount, "CASH"
        );
        System.out.println("Payment created: " + created);

        System.out.println("\n===== PAYMENT EXISTS TEST =====");
        System.out.println("Payment exists: "
                + paymentDAO.paymentExists(bookingId));

        System.out.println("\n===== UPDATE PAYMENT STATUS TEST =====");
        boolean updated = paymentDAO.updatePaymentStatus(
                bookingId, "SUCCESSFUL"
        );
        System.out.println("Status updated: " + updated);

        System.out.println("\n===== DUPLICATE PAYMENT TEST =====");
        boolean duplicate = paymentDAO.createPayment(
                bookingId, amount, "CASH"
        );
        System.out.println("Duplicate payment created: " + duplicate);
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
