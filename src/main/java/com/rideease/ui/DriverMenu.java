package com.rideease.ui;

import com.rideease.model.Driver;
import com.rideease.model.User;
import com.rideease.service.BookingService;
import com.rideease.service.DriverService;

import java.util.List;
import java.util.Scanner;

public class DriverMenu {

    private final Scanner scanner;
    private final DriverService driverService;
    private final BookingService bookingService;

    public DriverMenu(Scanner scanner, DriverService driverService, BookingService bookingService) {
        this.scanner = scanner;
        this.driverService = driverService;
        this.bookingService = bookingService;
    }

    public void show(User driverUser) {
        Driver driver = ensureDriverProfile(driverUser);
        if (driver == null) {
            return;
        }

        boolean running = true;
        while (running) {
            Driver current = driverService.getDriverById(driver.getDriverId());
            Integer activeRideId = bookingService.getActiveBookingIdForDriver(current.getDriverId());

            System.out.println("\n=== DRIVER MENU (" + driverUser.getFullName() + ") ===");
            System.out.println("Driver ID: " + current.getDriverId()
                    + " | Online: " + (current.isOnline() ? "YES" : "NO")
                    + " | City/Area: " + current.getCity() + " / " + current.getServiceArea()
                    + " | Active Ride: " + (activeRideId == null ? "None" : "#" + activeRideId));
            System.out.println("1. Toggle Online / Offline Status");
            System.out.println("2. Update City & Service Area");
            System.out.println("3. View Assigned Bookings");
            System.out.println("4. Accept Assigned Booking");
            System.out.println("5. Reject Assigned Booking");
            System.out.println("6. Complete Active Ride");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleToggleOnline(current);
                case "2" -> handleUpdateLocation(current);
                case "3" -> handleViewBookings(current.getDriverId());
                case "4" -> handleAcceptBooking(current.getDriverId());
                case "5" -> handleRejectBooking(current.getDriverId());
                case "6" -> handleCompleteRide(current.getDriverId());
                case "7" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private Driver ensureDriverProfile(User driverUser) {
        Driver existing = driverService.getDriverByUserId(driverUser.getUserId());
        if (existing != null) {
            return existing;
        }

        System.out.println("\nComplete your Driver Profile first:");
        System.out.print("License Number: ");
        String license = scanner.nextLine().trim();

        System.out.print("City: ");
        String city = scanner.nextLine().trim();

        System.out.print("Service Area: ");
        String serviceArea = scanner.nextLine().trim();

        System.out.print("Vehicle Type (BIKE / AUTO / MINI / SEDAN / SUV): ");
        String vehicleType = scanner.nextLine().trim().toUpperCase();

        try {
            Driver created = driverService.registerDriverProfile(
                    driverUser.getUserId(),
                    license,
                    city,
                    serviceArea,
                    vehicleType
            );
            driverService.setOnlineStatus(created.getDriverId(), true);
            System.out.println("Driver profile created and set to ONLINE.");
            return created;
        } catch (IllegalArgumentException e) {
            System.out.println("Profile setup failed: " + e.getMessage());
            return null;
        }
    }

    private void handleToggleOnline(Driver driver) {
        try {
            boolean nextStatus = !driver.isOnline();
            driverService.setOnlineStatus(driver.getDriverId(), nextStatus);
            System.out.println("Status updated to: " + (nextStatus ? "ONLINE" : "OFFLINE"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Could not update status: " + e.getMessage());
        }
    }

    private void handleUpdateLocation(Driver driver) {
        System.out.print("New City: ");
        String city = scanner.nextLine().trim();

        System.out.print("New Service Area: ");
        String serviceArea = scanner.nextLine().trim();

        try {
            driverService.updateLocation(driver.getDriverId(), city, serviceArea);
            System.out.println("Location updated.");
        } catch (IllegalArgumentException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private void handleViewBookings(int driverId) {
        List<BookingService.RideRecord> rides = bookingService.getBookingsForDriver(driverId);
        if (rides.isEmpty()) {
            System.out.println("No assigned bookings found.");
            return;
        }
        for (BookingService.RideRecord ride : rides) {
            System.out.println(
                    "Booking #" + ride.getBookingId()
                            + " | Passenger ID: " + ride.getPassengerId()
                            + " | " + ride.getPickupLocation() + " -> " + ride.getDestination()
                            + " | Status: " + ride.getStatus()
                            + " | Fare: " + ride.getEstimatedFare()
            );
        }
    }

    private void handleAcceptBooking(int driverId) {
        System.out.print("Enter Booking ID to accept: ");
        int bookingId = readInt();
        try {
            bookingService.acceptBooking(bookingId, driverId);
            System.out.println("Booking #" + bookingId + " accepted. Ride is now active.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Accept failed: " + e.getMessage());
        }
    }

    private void handleRejectBooking(int driverId) {
        System.out.print("Enter Booking ID to reject: ");
        int bookingId = readInt();
        try {
            boolean reassigned = bookingService.rejectBooking(bookingId, driverId);
            BookingService.RideRecord ride = bookingService.getBookingById(bookingId);
            if (reassigned && ride != null && ride.getDriverId() != null) {
                System.out.println("Booking rejected. Reassigned to Driver ID: " + ride.getDriverId());
            } else {
                System.out.println("Booking rejected. Status is now: " + (ride == null ? "UPDATED" : ride.getStatus()));
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Reject failed: " + e.getMessage());
        }
    }

    private void handleCompleteRide(int driverId) {
        System.out.print("Enter Active Booking ID to complete: ");
        int bookingId = readInt();
        try {
            bookingService.completeBooking(bookingId, driverId);
            BookingService.RideRecord ride = bookingService.getBookingById(bookingId);
            System.out.println("Ride #" + bookingId + " completed! Final Fare: " + ride.getFinalFare());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Complete failed: " + e.getMessage());
        }
    }

    private int readInt() {
        String raw = scanner.nextLine().trim();
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}