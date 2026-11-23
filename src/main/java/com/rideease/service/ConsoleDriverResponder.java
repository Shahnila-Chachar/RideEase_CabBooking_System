package com.rideease.service;

import com.rideease.model.Driver;
import java.util.Scanner;

/**
 * ConsoleDriverResponder — asks the real driver y/n on the console.
 */
public class ConsoleDriverResponder implements DriverResponder {

    private final Scanner scanner;

    public ConsoleDriverResponder() {
        this(new Scanner(System.in));
    }

    public ConsoleDriverResponder(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public boolean accepts(Driver driver) {
        if (driver == null) {
            return false;
        }
        System.out.print("Driver #" + driver.getDriverId()
                + " [" + driver.getLicenseNumber() + ", " + driver.getCity()
                + "] — accept this ride? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }
}