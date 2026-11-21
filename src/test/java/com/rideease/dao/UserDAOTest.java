package com.rideease.dao;

import com.rideease.model.User;

public class UserDAOTest {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        String email = "testpassenger2026@example.com";
        String phone = "03009998877";

        User user = userDAO.findByEmail(email);

        if (user == null) {
            System.out.println("Test user not found!");
            return;
        }

        System.out.println("===== PHONE EXISTS TEST =====");

        boolean phoneFound = userDAO.phoneExists(phone);

        if (phoneFound) {
            System.out.println("Phone number already exists!");
        } else {
            System.out.println("Phone number not found!");
        }

        System.out.println("\n===== UPDATE USER TEST =====");

        user.setFullName("Updated Test Passenger");

        boolean updated = userDAO.updateUser(user);

        if (updated) {
            System.out.println("User updated successfully!");
        } else {
            System.out.println("User update failed!");
        }

        System.out.println("\n===== VERIFY UPDATED USER =====");

        User updatedUser = userDAO.findById(user.getUserId());

        if (updatedUser != null) {
            System.out.println("Updated Name: "
                    + updatedUser.getFullName());
            System.out.println("Email: "
                    + updatedUser.getEmail());
        } else {
            System.out.println("User not found!");
        }
    }
}
