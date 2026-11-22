package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;

public class VehicleDAO {

    public boolean registerVehicle(Vehicle vehicle) {
        String query = "INSERT INTO VEHICLES "
                + "(driver_id, vehicle_type, vehicle_number, model, color) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, vehicle.getDriverId());
            statement.setString(2, vehicle.getVehicleType());
            statement.setString(3, vehicle.getVehicleNumber());
            statement.setString(4, vehicle.getModel());
            statement.setString(5, vehicle.getColor());

            if (statement.executeUpdate() == 0) return false;

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    vehicle.setVehicleId(keys.getInt(1));
                    Vehicle saved = findById(vehicle.getVehicleId());
                    if (saved != null) vehicle.setCreatedAt(saved.getCreatedAt());
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Vehicle registration failed: " + e.getMessage());
        }
        return false;
    }

    public Vehicle findById(int vehicleId) {
        String query = "SELECT * FROM VEHICLES WHERE vehicle_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, vehicleId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return mapVehicle(result);
            }
        } catch (SQLException e) {
            System.out.println("Vehicle search failed: " + e.getMessage());
        }
        return null;
    }

    public Vehicle findByDriverId(int driverId) {
        String query = "SELECT * FROM VEHICLES WHERE driver_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, driverId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return mapVehicle(result);
            }
        } catch (SQLException e) {
            System.out.println("Vehicle search failed: " + e.getMessage());
        }
        return null;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String query = "UPDATE VEHICLES SET vehicle_type = ?, vehicle_number = ?, "
                + "model = ?, color = ? WHERE vehicle_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, vehicle.getVehicleType());
            statement.setString(2, vehicle.getVehicleNumber());
            statement.setString(3, vehicle.getModel());
            statement.setString(4, vehicle.getColor());
            statement.setInt(5, vehicle.getVehicleId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Vehicle update failed: " + e.getMessage());
        }
        return false;
    }

    private Vehicle mapVehicle(ResultSet result) throws SQLException {
        Timestamp timestamp = result.getTimestamp("created_at");
        LocalDateTime createdAt = timestamp == null ? null : timestamp.toLocalDateTime();

        return new Vehicle(
                result.getInt("vehicle_id"),
                result.getInt("driver_id"),
                result.getString("vehicle_type"),
                result.getString("vehicle_number"),
                result.getString("model"),
                result.getString("color"),
                createdAt
        );
    }
}
