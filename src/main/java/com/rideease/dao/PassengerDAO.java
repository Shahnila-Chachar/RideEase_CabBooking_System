package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PassengerDAO {

    public boolean registerPassenger(Passenger passenger) {

        String query = "INSERT INTO PASSENGERS (user_id) VALUES (?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, passenger.getUserId());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                try (ResultSet resultSet = statement.getGeneratedKeys()) {

                    if (resultSet.next()) {

                        int passengerId = resultSet.getInt(1);
                        passenger.setPassengerId(passengerId);

                        String dateQuery =
                                "SELECT created_at FROM PASSENGERS "
                                + "WHERE passenger_id = ?";

                        try (PreparedStatement dateStatement =
                                     connection.prepareStatement(dateQuery)) {

                            dateStatement.setInt(1, passengerId);

                            try (ResultSet dateResult =
                                         dateStatement.executeQuery()) {

                                if (dateResult.next()) {
                                    Timestamp timestamp =
                                            dateResult.getTimestamp("created_at");

                                    if (timestamp != null) {
                                        passenger.setCreatedAt(
                                                timestamp.toLocalDateTime());
                                    }
                                }
                            }
                        }

                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Passenger registration failed: "
                    + e.getMessage());
        }

        return false;
    }

    public Passenger findByUserId(int userId) {

        String query =
                "SELECT * FROM PASSENGERS WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapPassenger(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Passenger search failed: "
                    + e.getMessage());
        }

        return null;
    }

    public Passenger findById(int passengerId) {

        String query =
                "SELECT * FROM PASSENGERS WHERE passenger_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, passengerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapPassenger(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Passenger search failed: "
                    + e.getMessage());
        }

        return null;
    }

    private Passenger mapPassenger(ResultSet resultSet)
            throws SQLException {

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp == null
                ? null : timestamp.toLocalDateTime();

        return new Passenger(
                resultSet.getInt("passenger_id"),
                resultSet.getInt("user_id"),
                createdAt
        );
    }
}
