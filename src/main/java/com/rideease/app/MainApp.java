package com.rideease.app;

import com.rideease.model.User;
import com.rideease.service.UserService;
import com.rideease.ui.AuthMenu;
import com.rideease.ui.PassengerMenu;
import com.rideease.ui.DriverMenu;

import java.util.Scanner;

public class MainApp {

    private static void showWelcomeBanner() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                                                  ");
        System.out.println("                  RIDEEASE                        ");
        System.out.println("          SMART RIDE BOOKING SYSTEM               ");
        System.out.println("                                                  ");
        System.out.println("       DSA-Based Transportation Solution          ");
        System.out.println("==================================================");
        System.out.println("              Welcome to RideEase!                ");
        System.out.println("--------------------------------------------------");
    }

    private static void showUserWelcome(User user) {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("  Welcome, " + user.getFullName());
        System.out.println("  Account Type: " + user.getRole());
        System.out.println("--------------------------------------------------");
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        UserService userService = new UserService();
        AuthMenu authMenu = new AuthMenu(userService, scanner);

        showWelcomeBanner();

        User loggedInUser = authMenu.start();

        if (loggedInUser != null) {

            showUserWelcome(loggedInUser);

            String role = String.valueOf(loggedInUser.getRole());

            if ("PASSENGER".equalsIgnoreCase(role)) {
                new PassengerMenu(loggedInUser, scanner).start();

            } else if ("DRIVER".equalsIgnoreCase(role)) {
                new DriverMenu(loggedInUser, scanner).start();

            } else {
                System.out.println("Access denied: Unsupported account role.");
            }
        }

        System.out.println();
        System.out.println("==================================================");
        System.out.println("          Thank you for choosing RideEase!        ");
        System.out.println("              Safe journeys, always.              ");
        System.out.println("==================================================");

        scanner.close();
    }
}