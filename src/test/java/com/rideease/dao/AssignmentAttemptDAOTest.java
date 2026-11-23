package com.rideease.dao;

import com.rideease.model.RideAssignmentAttempt;
import java.util.List;

public class AssignmentAttemptDAOTest {

    private static int passed = 0;

    public static void main(String[] args) {
        AssignmentAttemptDAO dao = new AssignmentAttemptDAO();

        System.out.println("== input guards (no database needed) ==");
        check(!dao.recordResponse(1, 1, "BOGUS"), "invalid status is refused before any SQL");
        check(!dao.recordResponse(1, 1, "pending"),
                "status values are case-sensitive (lowercase is refused)");

        System.out.println("\n== fail-soft behaviour ==");
        int attemptId = dao.recordAttempt(999999, 999999);
        check(attemptId == -1, "recordAttempt fails soft (returns -1, no exception)");
        check(!dao.recordResponse(999999, 999999, "ACCEPTED"),
                "recordResponse fails soft (returns false, no exception)");
        List<RideAssignmentAttempt> attempts = dao.findByBookingId(999999);
        check(attempts != null && attempts.isEmpty(),
                "findByBookingId returns an empty list for unknown bookings");

        System.out.println("\n== with MySQL running (team rule 2) ==");
        System.out.println("With MySQL up, the same calls exercise the real paths:");
        System.out.println("existing booking+driver ids log PENDING/ACCEPTED/REJECTED rows;");
        System.out.println("unknown ids still fail soft on the foreign keys. Full-flow");
        System.out.println("verification happens in the end-to-end test (Task 1).");

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