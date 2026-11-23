package com.rideease.service;

import com.rideease.dao.AssignmentAttemptDAO;
import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;
import java.time.LocalDateTime;

public class AssignmentRetryManagerAttemptsTest {

    private static int passed = 0;

    private static Driver makeDriver(int id) {
        Driver d = new Driver(id, id + 100, "LIC-" + id, "Sukkur",
                "Military Road", true, LocalDateTime.now());
        d.setVehicleType("CAB");
        return d;
    }

    private static class ScriptResponder implements DriverResponder {
        private final java.util.Set<Integer> acceptIds = new java.util.HashSet<>();
        private int asks = 0;

        ScriptResponder(int... accept) {
            for (int id : accept) {
                acceptIds.add(id);
            }
        }

        @Override
        public boolean accepts(Driver driver) {
            asks++;
            return acceptIds.contains(driver.getDriverId());
        }
    }

    private static DriverLinkedList poolOf(int... ids) {
        DriverLinkedList pool = new DriverLinkedList();
        for (int id : ids) {
            pool.addLast(makeDriver(id));
        }
        return pool;
    }

    public static void main(String[] args) {
        System.out.println("== EDGE: booking-aware overload guards ==");
        AssignmentRetryManager noDao = new AssignmentRetryManager(new DriverMatchingService());
        check(noDao.assignDriver(null, 5, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(1)) == AssignmentRetryManager.NO_DRIVER_AVAILABLE,
                "null pool returns NO_DRIVER_AVAILABLE");
        check(noDao.assignDriver(poolOf(1), 5, "Sukkur", "Military Road", "CAB",
                        null) == AssignmentRetryManager.NO_DRIVER_AVAILABLE,
                "null responder returns NO_DRIVER_AVAILABLE");

        System.out.println("\n== logging off (bookingId <= 0) behaves like the old API ==");
        check(noDao.assignDriver(poolOf(1), 0, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(1)) == 1,
                "bookingId = 0: first driver accepts");
        check(noDao.assignDriver(poolOf(1, 2), -1, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(2)) == 2,
                "bookingId = -1: rejection retries reach driver 2");
        check(noDao.assignDriver(poolOf(1), -1, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(9)) == AssignmentRetryManager.NO_DRIVER_AVAILABLE,
                "all reject with logging off returns NO_DRIVER_AVAILABLE");

        System.out.println("\n== resilience: a failing DAO must not break assignment ==");
        AssignmentRetryManager withDao =
                new AssignmentRetryManager(new DriverMatchingService(), new AssignmentAttemptDAO());
        check(withDao.assignDriver(poolOf(1, 2), 7, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(2)) == 2,
                "booking 7: assignment succeeds even though attempt logging failed");
        check(withDao.assignDriver(poolOf(1), 7, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(9)) == AssignmentRetryManager.NO_DRIVER_AVAILABLE,
                "booking 7: all-reject path also survives logging failures");
        check(withDao.assignDriver(poolOf(1, 2, 3), 0, "Sukkur", "Military Road", "CAB",
                        new ScriptResponder(3)) == 3,
                "bookingId = 0 with DAO configured: logging skipped, assignment works");

        System.out.println("\n== constants the database relies on ==");
        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE == -1, "NO_DRIVER_AVAILABLE stays -1");
        check("NO_DRIVER_AVAILABLE".equals(AssignmentRetryManager.NO_DRIVER_AVAILABLE_STATUS),
                "status text still matches the BOOKINGS status value");

        System.out.println("\nAll " + passed + " checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Test failed: " + message);
        }
        passed++;
        System.out.println("PASS: " + message);
    }
}