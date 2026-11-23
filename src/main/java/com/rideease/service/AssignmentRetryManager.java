package com.rideease.service;

import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;

/**
 * AssignmentRetryManager — assigns one booking to a driver, retrying
 * with the next driver whenever a driver rejects.
 */
public class AssignmentRetryManager {

    // Returned when no driver accepts. Driver ids start at 1, so -1 is safe.
    public static final int NO_DRIVER_AVAILABLE = -1;

    // Same text as the NO_DRIVER_AVAILABLE booking status in the database
    public static final String NO_DRIVER_AVAILABLE_STATUS = "NO_DRIVER_AVAILABLE";

    private final DriverMatchingService matchingService;

    public AssignmentRetryManager(DriverMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    // Returns the accepting driver's id, or NO_DRIVER_AVAILABLE
    public int assignDriver(DriverLinkedList drivers,
                            String city,
                            String serviceArea,
                            String vehicleType,
                            DriverResponder responder) {
        if (drivers == null || responder == null) {
            return NO_DRIVER_AVAILABLE;          // EDGE: nothing to work with
        }

        DriverLinkedList tried = new DriverLinkedList();   // rejected drivers

        while (true) {
            Driver driver = matchingService.findEligibleDriver(
                    drivers, tried, city, serviceArea, vehicleType);

            if (driver == null) {
                return NO_DRIVER_AVAILABLE;      // no driver remains
            }
            if (responder.accepts(driver)) {
                return driver.getDriverId();
            }
            tried.addLast(driver);               // rejected: never offer again
        }
    }
}