package com.rideease.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/rideease_db";

    public static Connection getConnection() throws SQLException{
        String username = System.getenv("RIDE_DB_USERNAME");
        String password = System.getenv("RIDE_DB_PASSWORD");

        if (username == null || password == null) {
            throw new SQLException("Database credentials are not configured.");
        }
        return DriverManager.getConnection(URL, username, password);

    }
}
