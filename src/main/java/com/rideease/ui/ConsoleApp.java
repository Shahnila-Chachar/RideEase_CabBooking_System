package com.rideease.ui;

import com.rideease.model.User;
import com.rideease.model.UserRole;
import com.rideease.service.BookingService;
import com.rideease.service.DriverService;
import com.rideease.service.FareService;
import com.rideease.service.UserService;

import java.util.Scanner;

public class ConsoleApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        FareService fareService = new FareService();
        DriverService driverService = new DriverService();
        BookingService bookingService = new BookingService(driverService, fareService);

        AuthMenu authMenu = new AuthMenu(userService, scanner);
        PassengerMenu passengerMenu = new PassengerMenu(scanner, bookingService, fareService);
        DriverMenu driverMenu = new DriverMenu(scanner, driverService, bookingService);

        boolean running = true;
        while (running) {
            System.out.println("\n=== RIDEEASE CONSOLE APPLICATION ===");
            System.out.println("1. Authentication (Register / Login)");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    User loggedInUser = authMenu.start();
                    if (loggedInUser != null) {
                        if (loggedInUser.getRole() == UserRole.PASSENGER) {
                            passengerMenu.show(loggedInUser);
                        } else if (loggedInUser.getRole() == UserRole.DRIVER) {
                            driverMenu.show(loggedInUser);
                        } else {
                            System.out.println("Logged in as " + loggedInUser.getFullName() + " (" + loggedInUser.getRole() + ")");
                        }
                    }
                }
                case "2" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }
}