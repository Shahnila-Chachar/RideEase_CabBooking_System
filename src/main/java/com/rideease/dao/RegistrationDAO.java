package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.User;
import com.rideease.model.Driver;
import com.rideease.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistrationDAO {

    public boolean registerPassenger(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                insertUser(connection, user);

                String query = "INSERT INTO PASSENGERS (user_id) VALUES (?)";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, user.getUserId());

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Passenger profile could not be created.");
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public boolean registerDriver(User user, Driver driver, Vehicle vehicle)
            throws SQLException {

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                insertUser(connection, user);

                String driverQuery = "INSERT INTO DRIVERS "
                        + "(user_id, license_number, city, service_area) "
                        + "VALUES (?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(
                        driverQuery, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, user.getUserId());
                    statement.setString(2, driver.getLicenseNumber());
                    statement.setString(3, driver.getCity());
                    statement.setString(4, driver.getServiceArea());

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Driver profile could not be created.");
                    }

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Driver ID was not generated.");
                        }

                        driver.setDriverId(keys.getInt(1));
                    }
                }

                String vehicleQuery = "INSERT INTO VEHICLES "
                        + "(driver_id, vehicle_type, vehicle_number, model, color) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(
                        vehicleQuery, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, driver.getDriverId());
                    statement.setString(2, vehicle.getVehicleType());
                    statement.setString(3, vehicle.getVehicleNumber());
                    statement.setString(4, vehicle.getModel());
                    statement.setString(5, vehicle.getColor());

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Vehicle profile could not be created.");
                    }

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Vehicle ID was not generated.");
                        }

                        vehicle.setVehicleId(keys.getInt(1));
                        vehicle.setDriverId(driver.getDriverId());
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void insertUser(Connection connection, User user)
            throws SQLException {

        String query = "INSERT INTO USERS "
                + "(full_name, email, phone, password_hash, role) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getRole().name());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("User account could not be created.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("User ID was not generated.");
                }

                user.setUserId(keys.getInt(1));
            }
        }
    }
}
