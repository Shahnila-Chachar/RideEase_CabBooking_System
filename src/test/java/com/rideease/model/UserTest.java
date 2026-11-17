
package com.rideease.model;

public class UserTest {

    public static void main(String[] args) {

        User user = new User(
                1,
                "Ayesha Imran",
                "ayeshaimran.com",
                "hashedPassword123",
                UserRole.PASSENGER
        );


        System.out.println("User ID: " + user.getUserId());
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password Hash: " + user.getPasswordHash());
        System.out.println("Role: " + user.getRole());


        user.setUserId(2);
        user.setFullName("Ayesha Updated");
        user.setEmail("updated@example.com");
        user.setPasswordHash("newHash456");
        user.setRole(UserRole.DRIVER);

        System.out.println("After Updating:");

        System.out.println("User ID: " + user.getUserId());
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password Hash: " + user.getPasswordHash());
        System.out.println("Role: " + user.getRole());


        System.out.println("\nAvailable Roles:");

        for (UserRole role : UserRole.values()) {
            System.out.println(role);
        }
    }
}
