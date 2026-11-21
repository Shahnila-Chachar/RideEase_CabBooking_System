
package com.rideease.model;

import java.time.LocalDateTime;

public class PassengerTest {

    public static void main(String[] args) {

        Passenger passenger = new Passenger(
                101,
                1,
                LocalDateTime.of(2026, 11, 17, 10, 30)
        );

        System.out.println("Passenger ID: " + passenger.getPassengerId());
        System.out.println("User ID: " + passenger.getUserId());
        System.out.println("Created At: " + passenger.getCreatedAt());


        passenger.setPassengerId(105);
        passenger.setUserId(7);
        passenger.setCreatedAt(LocalDateTime.of(2026, 11, 17, 12, 0));

        System.out.println("\nAfter Updating:");

        System.out.println("Passenger ID: " + passenger.getPassengerId());
        System.out.println("User ID: " + passenger.getUserId());
        System.out.println("Created At: " + passenger.getCreatedAt());
    }
}
