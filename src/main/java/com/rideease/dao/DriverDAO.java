package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Driver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DriverDAO {

    public boolean registerDriver(Driver driver) {

        String query = "INSERT INTO DRIVERS "
                + "(user_id, license_number, city, service_area) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, driver.getUserId());
            statement.setString(2, driver.getLicenseNumber());
            statement.setString(3, driver.getCity());
            statement.setString(4, driver.getServiceArea());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                try (ResultSet resultSet = statement.getGeneratedKeys()) {

                    if (resultSet.next()) {
                        driver.setDriverId(resultSet.getInt(1));
                    } else {
                        return false;
                    }
                }

                String selectQuery =
                        "SELECT created_at, is_online FROM DRIVERS "
                        + "WHERE driver_id = ?";

                try (PreparedStatement selectStatement =
                        connection.prepareStatement(selectQuery)) {

                    selectStatement.setInt(1, driver.getDriverId());

                    try (ResultSet resultSet =
                            selectStatement.executeQuery()) {

                        if (resultSet.next()) {

                            Timestamp timestamp =
                                    resultSet.getTimestamp("created_at");

                            driver.setCreatedAt(
                                    timestamp == null
                                            ? null
                                            : timestamp.toLocalDateTime()
                            );

                            driver.setOnline(
                                    resultSet.getBoolean("is_online")
                            );

                            return true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Driver registration failed: "
                    + e.getMessage());
        }

        return false;
    }

    public Driver findByUserId(int userId) {

        String query = "SELECT * FROM DRIVERS WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapDriver(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Driver search failed: "
                    + e.getMessage());
        }

        return null;
    }

    public Driver findById(int driverId) {

        String query = "SELECT * FROM DRIVERS WHERE driver_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, driverId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapDriver(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Driver search failed: "
                    + e.getMessage());
        }

        return null;
    }

    public List<Driver> findOnlineDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM DRIVERS WHERE is_online = TRUE ORDER BY driver_id";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                drivers.add(mapDriver(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Online driver search failed: " + e.getMessage());
        }

        return drivers;
    }
    public boolean updateAvailability(int driverId, boolean isOnline) {

        String query =
                "UPDATE DRIVERS SET is_online = ? WHERE driver_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setBoolean(1, isOnline);
            statement.setInt(2, driverId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Driver availability update failed: "
                    + e.getMessage());
        }

        return false;
    }

    public boolean updateDriver(Driver driver) {

        String query = "UPDATE DRIVERS SET "
                + "license_number = ?, city = ?, service_area = ? "
                + "WHERE driver_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setString(1, driver.getLicenseNumber());
            statement.setString(2, driver.getCity());
            statement.setString(3, driver.getServiceArea());
            statement.setInt(4, driver.getDriverId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Driver update failed: "
                    + e.getMessage());
        }

        return false;
    }

    private Driver mapDriver(ResultSet resultSet)
            throws SQLException {

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp == null
                ? null : timestamp.toLocalDateTime();

        return new Driver(
                resultSet.getInt("driver_id"),
                resultSet.getInt("user_id"),
                resultSet.getString("license_number"),
                resultSet.getString("city"),
                resultSet.getString("service_area"),
                resultSet.getBoolean("is_online"),
                createdAt
        );
    }
}
