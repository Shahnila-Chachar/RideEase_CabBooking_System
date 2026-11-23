package com.rideease.ui;

import com.rideease.model.User;
import com.rideease.service.BookingService;
import com.rideease.service.FareService;
import com.rideease.validation.InputValidator;

import java.util.List;
import java.util.Scanner;

public class PassengerMenu {

    private final Scanner scanner;
    private final BookingService bookingService;
    private final FareService fareService;

    public PassengerMenu(Scanner scanner, BookingService bookingService, FareService fareService) {
        this.scanner = scanner;
        this.bookingService = bookingService;
        this.fareService = fareService;
    }

    public void show(User passengerUser) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== PASSENGER MENU (" + passengerUser.getFullName() + ") ===");
            System.out.println("1. Book a Ride");
            System.out.println("2. View My Bookings");
            System.out.println("3. Cancel Pending Booking");
            System.out.println("4. Pay for Completed Ride");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleBookRide(passengerUser);
                case "2" -> handleViewBookings(passengerUser);
                case "3" -> handleCancelBooking(passengerUser);
                case "4" -> handlePayForRide(passengerUser);
                case "5" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleBookRide(User passengerUser) {
        System.out.print("City: ");
        String city = scanner.nextLine().trim();

        System.out.print("Service Area: ");
        String serviceArea = scanner.nextLine().trim();

        System.out.print("Pickup Location: ");
        String pickup = scanner.nextLine().trim();

        System.out.print("Destination: ");
        String destination = scanner.nextLine().trim();

        System.out.print("Vehicle Type (BIKE / AUTO / MINI / SEDAN / SUV): ");
        String vehicleType = scanner.nextLine().trim().toUpperCase();

        System.out.print("Estimated Distance (km): ");
        String distanceInput = scanner.nextLine().trim();

        try {
            double distanceKm = Double.parseDouble(distanceInput);
            double previewFare = fareService.calculateFare(distanceKm, vehicleType);
            System.out.println("Estimated Fare: " + previewFare);

            BookingService.RideRecord ride = bookingService.createBooking(
                    passengerUser.getUserId(),
                    city,
                    serviceArea,
                    pickup,
                    destination,
                    vehicleType,
                    distanceKm
            );

            System.out.println("Booking #" + ride.getBookingId() + " created. Status: " + ride.getStatus());
            if (ride.getDriverId() != null) {
                System.out.println("Assigned Driver ID: " + ride.getDriverId());
            } else if (BookingService.STATUS_NO_DRIVER_AVAILABLE.equals(ride.getStatus())) {
                System.out.println("Status: " + BookingService.STATUS_NO_DRIVER_AVAILABLE);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid numeric distance.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    private void handleViewBookings(User passengerUser) {
        List<BookingService.RideRecord> rides = bookingService.getBookingsByPassenger(passengerUser.getUserId());
        if (rides.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        for (BookingService.RideRecord ride : rides) {
            System.out.println(
                    "Booking #" + ride.getBookingId()
                            + " | " + ride.getPickupLocation() + " -> " + ride.getDestination()
                            + " | Type: " + ride.getVehicleType()
                            + " | Status: " + ride.getStatus()
                            + " | Driver ID: " + (ride.getDriverId() == null ? "None" : ride.getDriverId())
                            + " | Fare: " + ride.getEstimatedFare()
                            + " | Paid: " + (ride.isPaid() ? "YES (" + ride.getPaymentMethod() + ")" : "NO")
            );
        }
    }

    private void handleCancelBooking(User passengerUser) {
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = readInt();
        try {
            bookingService.cancelBooking(bookingId, passengerUser.getUserId());
            System.out.println("Booking #" + bookingId + " cancelled.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }

    private void handlePayForRide(User passengerUser) {
        System.out.print("Enter Completed Booking ID to pay: ");
        int bookingId = readInt();

        System.out.print("Payment Method (CASH / CARD / WALLET): ");
        String method = scanner.nextLine().trim();

        if (InputValidator.isEmptyOrBlank(method)) {
            System.out.println("Payment method is required.");
            return;
        }

        try {
            bookingService.processPayment(bookingId, passengerUser.getUserId(), method);
            BookingService.RideRecord ride = bookingService.getBookingById(bookingId);
            System.out.println("Payment successful! Amount paid: " + ride.getFinalFare());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Payment failed: " + e.getMessage());
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