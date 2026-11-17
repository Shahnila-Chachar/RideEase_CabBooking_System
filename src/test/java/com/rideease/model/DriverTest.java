
package com.rideease.model;

import java.time.LocalDateTime;

public class DriverTest {

    public static void main(String[] args) {


        Driver driver = new Driver(
                201,
                5,
                "DL-123456",
                "Sukkur",
                "Military Road",
                true,
                LocalDateTime.of(2026, 11, 17, 10, 30)
        );


        System.out.println("Driver ID: " + driver.getDriverId());
        System.out.println("User ID: " + driver.getUserId());
        System.out.println("License Number: " + driver.getLicenseNumber());
        System.out.println("City: " + driver.getCity());
        System.out.println("Service Area: " + driver.getServiceArea());
        System.out.println("Online Status: " + driver.isOnline());
        System.out.println("Created At: " + driver.getCreatedAt());


        driver.setDriverId(202);
        driver.setUserId(6);
        driver.setLicenseNumber("DL-789012");
        driver.setCity("Karachi");
        driver.setServiceArea("Gulshan");
        driver.setOnline(false);
        driver.setCreatedAt(LocalDateTime.of(2026, 11, 17, 12, 0));

        System.out.println("\nAfter Updating:");

        System.out.println("Driver ID: " + driver.getDriverId());
        System.out.println("User ID: " + driver.getUserId());
        System.out.println("License Number: " + driver.getLicenseNumber());
        System.out.println("City: " + driver.getCity());
        System.out.println("Service Area: " + driver.getServiceArea());
        System.out.println("Online Status: " + driver.isOnline());
        System.out.println("Created At: " + driver.getCreatedAt());
    }
}
