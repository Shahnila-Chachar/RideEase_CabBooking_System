package com.rideease.dao;

import com.rideease.model.Passenger;

public class PassengerDAOTest {

    public static void main(String[] args) {

        PassengerDAO passengerDAO = new PassengerDAO();

        int userId = 1;

        System.out.println("===== REGISTER PASSENGER TEST =====");

        Passenger passenger = passengerDAO.findByUserId(userId);

        if (passenger == null) {

            passenger = new Passenger(0, userId, null);

            boolean registered =
                    passengerDAO.registerPassenger(passenger);

            if (registered) {
                System.out.println("Passenger registered successfully!");
                System.out.println("Passenger ID: "
                        + passenger.getPassengerId());
            } else {
                System.out.println("Passenger registration failed!");
                return;
            }

        } else {
            System.out.println("Passenger already exists!");
        }

        System.out.println("\n===== FIND BY USER ID TEST =====");

        Passenger foundByUser =
                passengerDAO.findByUserId(userId);

        if (foundByUser != null) {
            System.out.println("Passenger found by User ID!");
            System.out.println("Passenger ID: "
                    + foundByUser.getPassengerId());
        } else {
            System.out.println("Passenger not found!");
            return;
        }

        System.out.println("\n===== FIND BY PASSENGER ID TEST =====");

        Passenger foundById =
                passengerDAO.findById(foundByUser.getPassengerId());

        if (foundById != null) {
            System.out.println("Passenger found by ID!");
            System.out.println("User ID: "
                    + foundById.getUserId());
            System.out.println("Created At: "
                    + foundById.getCreatedAt());
        } else {
            System.out.println("Passenger not found!");
        }
    }
}
