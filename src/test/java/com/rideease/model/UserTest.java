
package com.rideease.model;

public class UserTest {

    public static void main(String[] args) {


        User user = new User(
                1,
                "Ayesha",
                "ayesha@gmail.com",
                "12345",
                "PASSENGER"
        );


        System.out.println("ID: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Role: " + user.getRole());


        user.setName("Ayesha Imran");

        System.out.println("Updated Name: " + user.getName());
    }
}
