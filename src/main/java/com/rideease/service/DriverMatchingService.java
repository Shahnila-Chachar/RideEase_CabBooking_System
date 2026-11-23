package com.rideease.service;

import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;

public class DriverMatchingService {

    public Driver findEligibleDriver(
            DriverLinkedList drivers,
            DriverLinkedList excluded,
            String city,
            String serviceArea,
            String vehicleType) {

        if (drivers == null) {
            return null;
        }

        for (Driver driver : drivers) {
            if (isEligible(driver, excluded, city, serviceArea, vehicleType)) {
                return driver;
            }
        }

        return null;
    }

    private boolean isEligible(
            Driver driver,
            DriverLinkedList excluded,
            String city,
            String serviceArea,
            String vehicleType) {

        if (driver == null) {
            return false;
        }

        if (!driver.isOnline() || driver.isBusy()) {
            return false;
        }

        if (!sameText(driver.getCity(), city)) {
            return false;
        }

        if (!sameText(driver.getServiceArea(), serviceArea)) {
            return false;
        }

        if (!sameText(driver.getVehicleType(), vehicleType)) {
            return false;
        }

        if (excluded != null && excluded.contains(driver.getDriverId())) {
            return false;
        }

        return true;
    }

    private boolean sameText(String a, String b) {
        return a != null && b != null
                && a.trim().equalsIgnoreCase(b.trim());
    }
}
