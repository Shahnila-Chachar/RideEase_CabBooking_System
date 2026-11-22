package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VehicleDAOTest {

    public static void main(String[] args) {
        int driverId = findDriverWithoutVehicle();

        if (driverId == 0) {
            System.out.println("Test skipped: no driver without a registered vehicle was found.");
            return;
        }

        String uniqueNumber = "TEST-" + (System.currentTimeMillis() % 100000000);

        Vehicle vehicle = new Vehicle(
                0, driverId, "RICKSHAW",
                uniqueNumber, "Test Model", "Black", null
        );

        VehicleDAO dao = new VehicleDAO();

        System.out.println("===== REGISTER VEHICLE TEST =====");
        boolean registered = dao.registerVehicle(vehicle);
        System.out.println("Registered: " + registered);
        System.out.println("Vehicle ID: " + vehicle.getVehicleId());

        if (!registered) return;

        System.out.println("\n===== FIND BY VEHICLE ID TEST =====");
        Vehicle found = dao.findById(vehicle.getVehicleId());
        System.out.println("Found: " + (found != null));

        System.out.println("\n===== FIND BY DRIVER ID TEST =====");
        Vehicle foundByDriver = dao.findByDriverId(driverId);
        System.out.println("Found: " + (foundByDriver != null));
    }

    private static int findDriverWithoutVehicle() {
        String query = "SELECT d.driver_id FROM DRIVERS d "
                + "LEFT JOIN VEHICLES v ON d.driver_id = v.driver_id "
                + "WHERE v.vehicle_id IS NULL LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) return result.getInt(1);
        } catch (Exception e) {
            System.out.println("Driver lookup failed: " + e.getMessage());
        }
        return 0;
    }
}
