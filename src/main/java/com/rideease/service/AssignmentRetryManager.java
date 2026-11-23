package com.rideease.service;

import com.rideease.dao.AssignmentAttemptDAO;
import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;

/**
 * AssignmentRetryManager — assigns one booking to a driver, retrying
 * with the next driver whenever a driver rejects.
 *
 * Every offer and answer is written to RIDE_ASSIGNMENT_ATTEMPTS when a
 * booking id and an AssignmentAttemptDAO are provided. Logging failures
 * never break the assignment (the DAO fails soft).
 */
public class AssignmentRetryManager {

    // Returned when no driver accepts. Driver ids start at 1, so -1 is safe.
    public static final int NO_DRIVER_AVAILABLE = -1;

    // Same text as the NO_DRIVER_AVAILABLE booking status in the database
    public static final String NO_DRIVER_AVAILABLE_STATUS = "NO_DRIVER_AVAILABLE";

    private final DriverMatchingService matchingService;
    private final AssignmentAttemptDAO attemptDao;   // optional: attempt logging

    public AssignmentRetryManager(DriverMatchingService matchingService) {
        this(matchingService, null);
    }

    public AssignmentRetryManager(DriverMatchingService matchingService, AssignmentAttemptDAO attemptDao) {
        this.matchingService = matchingService;
        this.attemptDao = attemptDao;
    }

    // Original signature: no booking context, so nothing is logged.
    public int assignDriver(DriverLinkedList drivers,
                            String city,
                            String serviceArea,
                            String vehicleType,
                            DriverResponder responder) {
        return assignDriver(drivers, NO_DRIVER_AVAILABLE, city, serviceArea, vehicleType, responder);
    }

    // Booking-aware overload: bookingId > 0 turns on attempt logging.
    public int assignDriver(DriverLinkedList drivers,
                            int bookingId,
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

            boolean log = bookingId > 0 && attemptDao != null;
            if (log) {
                attemptDao.recordAttempt(bookingId, driver.getDriverId());
            }
            if (responder.accepts(driver)) {
                if (log) {
                    attemptDao.recordResponse(bookingId, driver.getDriverId(), "ACCEPTED");
                }
                return driver.getDriverId();
            }
            if (log) {
                attemptDao.recordResponse(bookingId, driver.getDriverId(), "REJECTED");
            }
            tried.addLast(driver);               // rejected: never offer again
        }
    }
}