package com.rideease.service;

import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;
import java.time.LocalDateTime;

public class DriverResponseManagerTest {

    private static int passed = 0;

    private static Driver makeDriver(int id) {
        Driver d = new Driver(id, id + 100, "LIC-" + id, "Sukkur",
                "Military Road", true, LocalDateTime.now());
        d.setVehicleType("CAB");
        return d;
    }

    // Scripted answer source: accepts only the listed ids, counts every ask.
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

    public static void main(String[] args) {
        System.out.println("== EDGE: guards run before anyone is asked ==");
        ScriptResponder script = new ScriptResponder(1);
        DriverResponseManager manager = new DriverResponseManager(script);

        check(!manager.accepts(null), "null driver returns false");
        check(script.asks == 0, "null driver never reaches the answer source");

        Driver offline = makeDriver(2);
        offline.setOnline(false);
        check(!manager.accepts(offline), "offline driver is refused without asking");
        check(script.asks == 0, "offline driver never reaches the answer source");

        Driver busy = makeDriver(3);
        busy.setBusy(true);
        check(!manager.accepts(busy), "busy driver is refused without asking");
        check(script.asks == 0, "busy driver never reaches the answer source");

        check(!new DriverResponseManager(null).accepts(makeDriver(4)),
                "missing answer source returns false");

        System.out.println("\n== accept flips busy, reject leaves the driver free ==");
        Driver accepting = makeDriver(1);
        check(manager.accepts(accepting), "driver 1 accepts");
        check(accepting.isBusy(), "driver 1 is busy right after accepting");
        int asksAfterAccept = script.asks;
        check(!manager.accepts(accepting), "busy driver is not offered a second ride");
        check(script.asks == asksAfterAccept, "busy guard blocks before asking again");

        Driver rejecting = makeDriver(5);
        check(!manager.accepts(rejecting), "driver 5 rejects");
        check(!rejecting.isBusy(), "rejected driver stays free");

        System.out.println("\n== completeRide frees the driver ==");
        check(manager.completeRide(accepting), "completed ride frees driver 1");
        check(!accepting.isBusy(), "driver 1 is available again");
        check(!manager.completeRide(accepting), "completing a free driver returns false");
        check(!manager.completeRide(null), "completeRide(null) returns false");
        check(manager.accepts(accepting), "freed driver can accept a new booking");
        check(manager.completeRide(accepting), "cleanup: driver 1 freed again");

        System.out.println("\n== setOnline toggles availability ==");
        check(manager.setOnline(accepting, false) && !accepting.isOnline(),
                "setOnline(false) takes the driver offline");
        check(!manager.accepts(accepting), "offline driver is not offered rides");
        check(manager.setOnline(accepting, true) && accepting.isOnline(),
                "setOnline(true) brings the driver back online");

        System.out.println("\n== composition: retry loop + response manager ==");
        DriverLinkedList pool = new DriverLinkedList();
        pool.addLast(makeDriver(1));
        pool.addLast(makeDriver(2));
        pool.addLast(makeDriver(3));
        ScriptResponder retryScript = new ScriptResponder(1);
        DriverResponseManager responses = new DriverResponseManager(retryScript);
        AssignmentRetryManager retry = new AssignmentRetryManager(new DriverMatchingService());

        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE == -1,
                "NO_DRIVER_AVAILABLE stays -1");

        int first = retry.assignDriver(pool, "Sukkur", "Military Road", "CAB", responses);
        check(first == 1, "first booking auto-assigned to driver 1");
        check(retryScript.asks == 1, "first booking: only driver 1 was asked");
        Driver driver1 = pool.findById(1);
        check(driver1 != null && driver1.isBusy(), "driver 1 busy after auto-accept");

        int second = retry.assignDriver(pool, "Sukkur", "Military Road", "CAB", responses);
        check(second == AssignmentRetryManager.NO_DRIVER_AVAILABLE,
                "second booking finds nobody: driver 1 busy, others scripted to refuse");
        check(retryScript.asks == 3,
                "second booking: drivers 2 and 3 asked, busy driver 1 skipped silently");

        check(responses.completeRide(driver1), "driver 1 completes the ride");
        int third = retry.assignDriver(pool, "Sukkur", "Military Road", "CAB", responses);
        check(third == 1, "freed driver 1 gets the next booking");
        check(retryScript.asks == 4, "third booking: only driver 1 asked again");

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