package com.rideease.ui;

import com.rideease.dao.*;
import com.rideease.model.*;
import com.rideease.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class PassengerMenu {

    private final User user;
    private final Scanner scanner;
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final FareService fareService = new FareService();
    private final BookingService bookingService;
    private final ConsoleDriverResponder responder;

    public PassengerMenu(User user, Scanner scanner) {
        this.user = user;
        this.scanner = scanner;
        this.responder = new ConsoleDriverResponder(scanner);

        this.bookingService = new BookingService(
                bookingDAO,
                new DriverDAO(),
                new VehicleDAO(),
                new AssignmentAttemptDAO()
        );
    }

    public void start() {
        Passenger passenger = passengerDAO.findByUserId(user.getUserId());

        if (passenger == null) {
            System.out.println("Passenger profile not found.");
            return;
        }

        boolean running = true;

        while (running) {
            System.out.println("\n========== PASSENGER MENU ==========");
            System.out.println("1. Book a Ride");
            System.out.println("2. Booking History");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    bookRide(passenger);
                    break;
                case "2":
                    showHistory(passenger);
                    break;
                case "3":
                    running = false;
                    System.out.println("Passenger logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void bookRide(Passenger passenger) {
        System.out.println("\n========== SELECT PICKUP ==========");
        LocationCatalog.displayLocations();

        LocationCatalog.Location pickup =
                selectLocation("Enter pickup number: ");

        if (pickup == null) {
            System.out.println("Invalid pickup selection.");
            return;
        }

        System.out.println("\n========== SELECT DESTINATION ==========");
        LocationCatalog.displayLocations();

        LocationCatalog.Location destination =
                selectLocation("Enter destination number: ");

        if (destination == null) {
            System.out.println("Invalid destination selection.");
            return;
        }

        if (pickup.getName().equalsIgnoreCase(destination.getName())) {
            System.out.println("Pickup and destination cannot be same.");
            return;
        }

        System.out.println("\n1. RICKSHAW");
        System.out.println("2. CAB");
        System.out.print("Select vehicle: ");

        String vehicleChoice = scanner.nextLine().trim();
        String vehicleType;

        if (vehicleChoice.equals("1")) {
            vehicleType = "RICKSHAW";
        } else if (vehicleChoice.equals("2")) {
            vehicleType = "CAB";
        } else {
            System.out.println("Invalid vehicle selection.");
            return;
        }

        try {
            var route = fareService.calculateRoute(
                    pickup.getName(), destination.getName());

            BigDecimal fare = fareService.calculateFare(
                    pickup.getName(), destination.getName(), vehicleType);

            System.out.println("\n========== RIDE ESTIMATE ==========");
            System.out.println("Pickup: " + pickup.getName());
            System.out.println("Destination: " + destination.getName());
            System.out.println("Vehicle: " + vehicleType);
            System.out.println("Route: " + String.join(" -> ", route.getLocations()));
            System.out.println("Simulated distance: " + route.getDistance() + " km");
            System.out.println("Estimated fare: Rs. " + fare);

            System.out.print("\nConfirm booking? (y/n): ");

            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("Booking cancelled.");
                return;
            }

            Booking booking = new Booking(
                    0,
                    passenger.getPassengerId(),
                    null,
                    null,
                    pickup.getName(),
                    destination.getName(),
                    vehicleType,
                    fare,
                    null,
                    "PENDING",
                    LocalDateTime.now()
            );

            boolean assigned = bookingService.createAndAssign(
                    booking, responder);

            if (assigned) {
                System.out.println("Booking confirmed!");
                System.out.println("Booking ID: " + booking.getBookingId());
            } else {
                System.out.println("Booking could not be assigned to a driver.");
                System.out.println("Check booking history for its status.");
            }

        } catch (Exception e) {
            System.out.println("Ride booking failed: " + e.getMessage());
        }
    }

    private LocationCatalog.Location selectLocation(String prompt) {
        System.out.print(prompt);

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return LocationCatalog.getLocation(choice);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showHistory(Passenger passenger) {
        System.out.println("\n========== BOOKING HISTORY ==========");

        List<Booking> bookings =
                bookingDAO.findByPassengerId(passenger.getPassengerId());

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("--------------------------------");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Pickup: " + booking.getPickupLocation());
            System.out.println("Destination: " + booking.getDestination());
            System.out.println("Vehicle: " + booking.getVehicleType());
            System.out.println("Estimated Fare: Rs. " + booking.getEstimatedFare());
            System.out.println("Status: " + booking.getStatus());
            System.out.println("Date: " + booking.getCreatedAt());
        }
    }
}