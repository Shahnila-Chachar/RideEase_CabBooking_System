package com.rideease.database;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBConnectionTest {
    public static void main(String[] args) {
        try(
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DATABASE()")
        ){
            if (resultSet.next()) {
                System.out.println("Database connected successfully!");
                System.out.println("Database: " + resultSet.getString(1));
            }
        } catch (SQLException e){
            System.out.println("Database Connection Failed!");
            System.out.println(e.getMessage());
        }
    }
}
