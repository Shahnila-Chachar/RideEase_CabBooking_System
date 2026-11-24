package com.rideease.ui;

import com.rideease.model.User;
import com.rideease.model.UserRole;
import com.rideease.service.UserService;
import com.rideease.validation.InputValidator;

import java.util.Scanner;

public class AuthMenu {

    private final UserService userService;
    private final Scanner scanner;

    public AuthMenu(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    public User start() {
        while (true) {
            System.out.println("\n=== RideEase Authentication ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleRegister();
                    break;
                case "2":
                    User user = handleLogin();
                    if (user != null) return user;
                    break;
                case "3":
                    System.out.println("Exiting RideEase. Goodbye!");
                    return null;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    private void handleRegister() {
        System.out.println("\n--- Register New Account ---");

        String fullName = promptValidName();
        String email = promptValidEmail();
        String phone = promptValidPhone();
        String password = promptValidPassword();
        UserRole role = promptValidRole();

        try {
            User createdUser;

            if (role == UserRole.PASSENGER) {
                createdUser = userService.registerPassenger(
                        fullName, email, phone, password);
            } else {
                System.out.println("\n--- Driver Details ---");

                String license = promptNonEmpty("Enter license number: ");
                String city = com.rideease.service.LocationCatalog.CITY;
                String serviceArea = com.rideease.service.LocationCatalog.SERVICE_AREA;
                System.out.println("Service area: " + serviceArea + ", " + city);

                String vehicleType;
                while (true) {
                    System.out.println("Vehicle type:");
                    System.out.println("1. RICKSHAW");
                    System.out.println("2. CAB");
                    System.out.print("Choose: ");

                    String choice = scanner.nextLine().trim();

                    if (choice.equals("1")) {
                        vehicleType = "RICKSHAW";
                        break;
                    }

                    if (choice.equals("2")) {
                        vehicleType = "CAB";
                        break;
                    }

                    System.out.println("Please choose 1 or 2.");
                }

                String vehicleNumber = promptNonEmpty("Enter vehicle number: ");
                String model = promptNonEmpty("Enter vehicle model: ");
                String color = promptNonEmpty("Enter vehicle color: ");

                createdUser = userService.registerDriver(
                        fullName, email, phone, password,
                        license, city, serviceArea,
                        vehicleType, vehicleNumber, model, color
                );
            }

            System.out.println("Registration successful! Welcome, "
                    + createdUser.getFullName()
                    + " (" + createdUser.getRole() + ").");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private User handleLogin() {
        System.out.println("\n--- User Login ---");

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (InputValidator.isEmptyOrBlank(email)
                || InputValidator.isEmptyOrBlank(password)) {
            System.out.println("Login failed: Email and password cannot be empty.");
            return null;
        }

        try {
            User user = userService.login(email, password);

            System.out.println("Login successful! Welcome back, "
                    + user.getFullName() + " [" + user.getRole() + "].");

            return user;

        } catch (IllegalArgumentException e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    private String promptValidName() {
        while (true) {
            System.out.print("Enter full name: ");
            String name = scanner.nextLine();

            if (InputValidator.isValidName(name)) return name.trim();

            System.out.println("Invalid name. Use at least 2 alphabetic characters.");
        }
    }

    private String promptValidEmail() {
        while (true) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            if (!InputValidator.isValidEmail(email)) {
                System.out.println("Invalid email format.");
                continue;
            }

            if (userService.isEmailRegistered(email)) {
                System.out.println("This email is already registered.");
                continue;
            }

            return email;
        }
    }

    private String promptValidPhone() {
        while (true) {
            System.out.print("Enter phone number: ");
            String phone = scanner.nextLine().trim();

            if (InputValidator.isValidPhoneNumber(phone)) return phone;

            System.out.println("Invalid phone number. Please try again.");
        }
    }

    private String promptValidPassword() {
        while (true) {
            System.out.print("Enter password (min 6 characters): ");
            String password = scanner.nextLine();

            if (InputValidator.isValidPassword(password)) return password;

            System.out.println("Password must be at least 6 characters long.");
        }
    }

    private UserRole promptValidRole() {
        while (true) {
            System.out.println("Select role:");
            System.out.println("1. PASSENGER");
            System.out.println("2. DRIVER");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) return UserRole.PASSENGER;
            if (choice.equals("2")) return UserRole.DRIVER;

            System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }

    private String promptNonEmpty(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) return value;

            System.out.println("This field cannot be empty.");
        }
    }
}
