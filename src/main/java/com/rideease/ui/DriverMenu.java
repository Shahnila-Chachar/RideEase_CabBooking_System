package com.rideease.ui;

import com.rideease.dao.BookingDAO;
import com.rideease.dao.DriverDAO;
import com.rideease.model.Booking;
import com.rideease.model.Driver;
import com.rideease.model.User;

import java.util.List;
import java.util.Scanner;

public class DriverMenu {

    private final User user;
    private final Scanner scanner;
    private final DriverDAO driverDAO = new DriverDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public DriverMenu(User user, Scanner scanner) {
        this.user = user;
        this.scanner = scanner;
    }

    public void start() {
        Driver driver = driverDAO.findByUserId(user.getUserId());

        if (driver == null) {
            System.out.println("Driver profile not found.");
            return;
        }

        if (!driver.isOnline()) {
            if (driverDAO.updateAvailability(driver.getDriverId(), true)) {
                driver.setOnline(true);
                System.out.println("\nYou are now ONLINE and available for rides.");
            } else {
                System.out.println("\nCould not update driver availability.");
            }
        } else {
            System.out.println("\nYou are already ONLINE.");
        }

        boolean running = true;

        while (running && scanner.hasNextLine()) {
            System.out.println("\n========== DRIVER MENU ==========");
            System.out.println("1. View Assigned Rides");
            System.out.println("2. Update Ride Status");
            System.out.println("3. Toggle Online/Offline");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showRides(driver.getDriverId());
                    break;
                case "2":
                    updateRideStatus(driver.getDriverId());
                    break;
                case "3":
                    toggleAvailability(driver);
                    break;
                case "4":
                    running = false;
                    System.out.println("Driver logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void toggleAvailability(Driver driver) {
        boolean newStatus = !driver.isOnline();

        if (driverDAO.updateAvailability(driver.getDriverId(), newStatus)) {
            driver.setOnline(newStatus);
            System.out.println(newStatus
                    ? "You are now ONLINE and available for rides."
                    : "You are now OFFLINE.");
        } else {
            System.out.println("Could not update availability.");
        }
    }

    private void showRides(int driverId) {
        List<Booking> bookings = bookingDAO.findByDriverId(driverId);

        System.out.println("\n========== ASSIGNED RIDES ==========");

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No assigned rides found.");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("--------------------------------");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Pickup: " + booking.getPickupLocation());
            System.out.println("Destination: " + booking.getDestination());
            System.out.println("Vehicle: " + booking.getVehicleType());
            System.out.println("Status: " + booking.getStatus());
            System.out.println("Fare: Rs. " + booking.getEstimatedFare());
        }
    }

    private void updateRideStatus(int driverId) {
        List<Booking> bookings = bookingDAO.findByDriverId(driverId);

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No assigned rides found.");
            return;
        }

        System.out.print("Enter booking ID: ");

        int bookingId;
        try {
            bookingId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid booking ID.");
            return;
        }

        Booking selected = null;

        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                selected = booking;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Ride not found for this driver.");
            return;
        }

        String newStatus;

        if ("ACCEPTED".equalsIgnoreCase(selected.getStatus())) {
            newStatus = "IN_PROGRESS";
        } else if ("IN_PROGRESS".equalsIgnoreCase(selected.getStatus())) {
            newStatus = "COMPLETED";
        } else {
            System.out.println("This ride cannot be updated. Current status: "
                    + selected.getStatus());
            return;
        }

        bookingDAO.updateStatus(bookingId, newStatus);
        System.out.println("Ride status updated to " + newStatus);
    }
}