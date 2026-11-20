package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DriverDAOTest {

    public static void main(String[] args) {

        int userId = createTestUser();

        if (userId == 0) {
            System.out.println("Test user creation failed.");
            return;
        }

        DriverDAO driverDAO = new DriverDAO();

        System.out.println("\n===== REGISTER DRIVER TEST =====");

        Driver driver = new Driver(
                0,
                userId,
                "DL-TEST-" + userId,
                "Sukkur",
                "Military Road",
                false,
                null
        );

        boolean registered = driverDAO.registerDriver(driver);

        System.out.println("Registration successful: " + registered);
        System.out.println("Generated Driver ID: " + driver.getDriverId());
        System.out.println("Initial Online Status: " + driver.isOnline());
        System.out.println("Created At: " + driver.getCreatedAt());

        if (!registered) {
            return;
        }

        System.out.println("\n===== FIND BY USER ID TEST =====");

        Driver foundByUser = driverDAO.findByUserId(userId);

        if (foundByUser != null) {
            System.out.println("Driver found by User ID!");
            System.out.println("Driver ID: " + foundByUser.getDriverId());
        }

        System.out.println("\n===== FIND BY DRIVER ID TEST =====");

        Driver foundById = driverDAO.findById(driver.getDriverId());

        if (foundById != null) {
            System.out.println("Driver found by ID!");
            System.out.println("License Number: " + foundById.getLicenseNumber());
        }

        System.out.println("\n===== UPDATE AVAILABILITY TEST =====");

        boolean availabilityUpdated =
                driverDAO.updateAvailability(driver.getDriverId(), true);

        System.out.println("Availability updated: " + availabilityUpdated);

        Driver updatedDriver = driverDAO.findById(driver.getDriverId());

        if (updatedDriver != null) {
            System.out.println("Online Status: " + updatedDriver.isOnline());
        }

        System.out.println("\n===== UPDATE DRIVER TEST =====");

        updatedDriver.setCity("Karachi");
        updatedDriver.setServiceArea("Gulshan");
        updatedDriver.setLicenseNumber("DL-UPDATED-" + userId);

        boolean driverUpdated = driverDAO.updateDriver(updatedDriver);

        System.out.println("Driver updated: " + driverUpdated);

        Driver finalDriver = driverDAO.findById(driver.getDriverId());

        if (finalDriver != null) {
            System.out.println("Updated City: " + finalDriver.getCity());
            System.out.println("Updated Service Area: " + finalDriver.getServiceArea());
            System.out.println("Updated License: " + finalDriver.getLicenseNumber());
        }

        driverDAO.updateAvailability(driver.getDriverId(), false);
        System.out.println("\nDriver returned to offline status.");
    }

    private static int createTestUser() {

        String query = "INSERT INTO USERS "
                + "(full_name, email, phone, password_hash, role) "
                + "VALUES (?, ?, ?, ?, 'DRIVER')";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS)
        ) {

            long uniqueValue = System.currentTimeMillis();

            statement.setString(1, "Driver DAO Test");
            statement.setString(2, "driver.test." + uniqueValue + "@example.com");
            statement.setString(3, "03" + String.valueOf(uniqueValue).substring(2));
            statement.setString(4, "test-password-hash");

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (Exception e) {
            System.out.println("Test user creation failed: " + e.getMessage());
        }

        return 0;
    }
}

