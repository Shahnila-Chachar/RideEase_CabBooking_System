package com.rideease.service;

import com.rideease.dao.BookingDAO;
import com.rideease.dao.DriverDAO;
import com.rideease.dao.VehicleDAO;
import com.rideease.database.DBConnection;
import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Booking;
import com.rideease.model.Driver;
import com.rideease.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DriverPoolLoaderTest {

    public static void main(String[] args) {
        VehicleDAO vehicleDAO = new VehicleDAO();
        BookingDAO bookingDAO = new BookingDAO();
        DriverPoolLoader loader = new DriverPoolLoader(vehicleDAO, bookingDAO);

        System.out.println("== EDGE: no database needed ==");
        check(loader.loadDrivers(null).isEmpty(), "null list gives an empty list");
        check(loader.loadDrivers(new ArrayList<>()).isEmpty(),
                "empty list gives an empty list");
        List<Driver> withNull = new ArrayList<>();
        withNull.add(null);
        check(loader.loadDrivers(withNull).isEmpty(), "null entry is skipped");

        System.out.println("\n== database check (needs MySQL) ==");
        int driverId = findFirstDriverId();
        if (driverId == 0) {
            System.out.println("Database check skipped: no driver found.");
            return;
        }

        Driver driver = new DriverDAO().findById(driverId);
        List<Driver> input = new ArrayList<>();
        input.add(driver);
        DriverLinkedList loaded = loader.loadDrivers(input);

        Vehicle vehicle = vehicleDAO.findByDriverId(driverId);
        if (vehicle == null) {
            check(loaded.isEmpty(), "driver without a vehicle is left out");
        } else {
            Driver result = loaded.findById(driverId);
            check(result != null, "driver with a vehicle is loaded");
            check(vehicle.getVehicleType().equals(result.getVehicleType()),
                    "vehicleType comes from the VEHICLES table: "
                            + result.getVehicleType());
            check(result.isBusy() == hasActiveBooking(bookingDAO, driverId),
                    "busy matches the driver's active bookings: " + result.isBusy());
        }

        System.out.println("\nAll checks passed.");
    }

    // checked separately from the loader, to compare against it
    private static boolean hasActiveBooking(BookingDAO bookingDAO, int driverId) {
        for (Booking booking : bookingDAO.findByDriverId(driverId)) {
            if ("ACCEPTED".equals(booking.getStatus())
                    || "IN_PROGRESS".equals(booking.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private static int findFirstDriverId() {
        String query = "SELECT driver_id FROM DRIVERS LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Driver lookup failed: " + e.getMessage());
        }
        return 0;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Test failed: " + message);
        }
        System.out.println("PASS: " + message);
    }
}