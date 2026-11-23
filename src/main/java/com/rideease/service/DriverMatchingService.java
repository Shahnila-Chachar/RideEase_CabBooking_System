package com.rideease.service;

import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;

/**
 * DriverMatchingService — finds an eligible driver for a booking.

 * Note: Text comparison ignores upper/lower case and extra spaces
 * (" sukkur " = "Sukkur").
 */
public class DriverMatchingService {

    public Driver findEligibleDriver(DriverLinkedList drivers,
                                     DriverLinkedList excluded,
                                     String city,
                                     String serviceArea,
                                     String vehicleType) {
        if (drivers == null) {
            return null;                         // EDGE: no list given
        }

        for (Driver driver : drivers) {
            if (isEligible(driver, excluded, city, serviceArea, vehicleType)) {
                return driver;                   // first eligible driver wins
            }
        }
        return null;                             // nobody matched
    }

    private boolean isEligible(Driver driver,
                               DriverLinkedList excluded,
                               String city,
                               String serviceArea,
                               String vehicleType) {
        if (driver == null) {
            return false;                        // EDGE: null entry
        }
        if (!driver.isOnline()) {
            return false;                        // skip offline drivers
        }
        if (driver.isBusy()) {
            return false;                        // skip busy drivers
        }
        if (!sameText(driver.getCity(), city)) {
            return false;                        // wrong city
        }
        if (!sameText(driver.getServiceArea(), serviceArea)) {
            return false;                        // wrong service area
        }
        if (!sameText(driver.getVehicleType(), vehicleType)) {
            return false;                        // wrong vehicle type
        }
        if (excluded != null && excluded.contains(driver.getDriverId())) {
            return false;                        // already rejected this booking
        }
        return true;
    }

    // true only when both texts exist and match,
    private boolean sameText(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }
}