
package com.rideease.dao;

import com.rideease.database.DBConnection;
import com.rideease.model.User;
import com.rideease.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public boolean registerUser(User user) {

        String query = "INSERT INTO USERS "
                + "(full_name, email, phone, password_hash, role) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getRole().name());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                try (ResultSet resultSet = statement.getGeneratedKeys()) {

                    if (resultSet.next()) {
                        user.setUserId(resultSet.getInt(1));
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("User registration failed: " + e.getMessage());
        }

        return false;
    }

    public User findByEmail(String email) {

        String query = "SELECT * FROM USERS WHERE email = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("password_hash"),
                            UserRole.valueOf(resultSet.getString("role"))
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("User search failed: " + e.getMessage());
        }

        return null;
    }

    public User findById(int userId) {

        String query = "SELECT * FROM USERS WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("password_hash"),
                            UserRole.valueOf(resultSet.getString("role"))
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("User search failed: " + e.getMessage());
        }

        return null;
    }

    public boolean emailExists(String email) {

        String query = "SELECT user_id FROM USERS WHERE email = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Email check failed: " + e.getMessage());
        }

        return false;
    }

    public boolean phoneExists(String phone) {

        String query = "SELECT user_id FROM USERS WHERE phone = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setString(1, phone);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Phone check failed: " + e.getMessage());
        }

        return false;
    }

    public boolean updateUser(User user) {

        String query = "UPDATE USERS "
                + "SET full_name = ?, email = ?, phone = ?, "
                + "password_hash = ?, role = ? "
                + "WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getRole().name());
            statement.setInt(6, user.getUserId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("User update failed: " + e.getMessage());
        }

        return false;
    }
}
