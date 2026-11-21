package com.rideease.service;

import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;

import java.time.LocalDateTime;

public class AssignmentRetryManagerTest {

    private static int passed = 0;

    private static Driver makeDriver(int id) {
        Driver d = new Driver(id, id + 100, "LIC-" + id, "Sukkur",
                "Military Road", true, LocalDateTime.now());
        d.setVehicleType("CAB");
        d.setBusy(false);
        return d;
    }

    private static class FakeResponder implements DriverResponder {
        private final int[] acceptIds;
        private final StringBuilder offered = new StringBuilder();
        private int offerCount = 0;

        FakeResponder(int... acceptIds) {
            this.acceptIds = acceptIds;
        }

        @Override
        public boolean accepts(Driver driver) {
            offerCount++;
            offered.append(driver.getDriverId()).append(" ");
            for (int id : acceptIds) {
                if (id == driver.getDriverId()) {
                    return true;
                }
            }
            return false;
        }
    }

    private static String assign(AssignmentRetryManager manager,
                                 DriverLinkedList drivers,
                                 DriverResponder responder) {
        return manager.assignDriver(
                drivers, "Sukkur", "Military Road", "CAB", responder);
    }

    public static void main(String[] args) {
        AssignmentRetryManager manager =
                new AssignmentRetryManager(new DriverMatchingService());

        System.out.println("== EDGE: nothing to assign ==");
        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE.equals(
                        assign(manager, new DriverLinkedList(), new FakeResponder())),
                "empty list returns NO_DRIVER_AVAILABLE");
        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE.equals(
                        assign(manager, null, new FakeResponder())),
                "null list returns NO_DRIVER_AVAILABLE");
        DriverLinkedList some = new DriverLinkedList();
        some.addLast(makeDriver(1));
        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE.equals(
                        assign(manager, some, null)),
                "null responder returns NO_DRIVER_AVAILABLE");

        System.out.println("\n== first driver accepts ==");
        DriverLinkedList three = new DriverLinkedList();
        three.addLast(makeDriver(1));
        three.addLast(makeDriver(2));
        three.addLast(makeDriver(3));
        FakeResponder acceptsFirst = new FakeResponder(1);
        check("1".equals(assign(manager, three, acceptsFirst)),
                "driver 1 is assigned");
        check(acceptsFirst.offerCount == 1,
                "only one driver was asked");

        System.out.println("\n== first rejects, second accepts ==");
        FakeResponder acceptsSecond = new FakeResponder(2);
        check("2".equals(assign(manager, three, acceptsSecond)),
                "driver 2 is assigned after driver 1 rejects");
        check("1 2 ".contentEquals(acceptsSecond.offered),
                "drivers were asked in order: 1 then 2");

        System.out.println("\n== all drivers reject ==");
        FakeResponder rejectsAll = new FakeResponder();
        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE.equals(
                        assign(manager, three, rejectsAll)),
                "all rejected returns NO_DRIVER_AVAILABLE");
        check(rejectsAll.offerCount == 3,
                "each driver was asked exactly once");
        check("1 2 3 ".contentEquals(rejectsAll.offered),
                "no driver was asked twice");

        System.out.println("\n== ineligible drivers are never offered ==");
        Driver offline = makeDriver(10);
        offline.setOnline(false);
        Driver busy = makeDriver(11);
        busy.setBusy(true);
        Driver wrongCity = makeDriver(12);
        wrongCity.setCity("Karachi");
        DriverLinkedList mixed = new DriverLinkedList();
        mixed.addLast(offline);
        mixed.addLast(busy);
        mixed.addLast(wrongCity);
        mixed.addLast(makeDriver(13));
        FakeResponder acceptsLast = new FakeResponder(13);
        check("13".equals(assign(manager, mixed, acceptsLast)),
                "only eligible driver 13 is assigned");
        check("13 ".contentEquals(acceptsLast.offered),
                "ineligible drivers were never asked");

        System.out.println("\n== no eligible drivers at all ==");
        DriverLinkedList noneEligible = new DriverLinkedList();
        noneEligible.addLast(offline);
        noneEligible.addLast(busy);
        FakeResponder neverAsked = new FakeResponder(10, 11);
        check(AssignmentRetryManager.NO_DRIVER_AVAILABLE.equals(
                        assign(manager, noneEligible, neverAsked)),
                "no eligible driver returns NO_DRIVER_AVAILABLE");
        check(neverAsked.offerCount == 0,
                "nobody was asked");

        System.out.println("\n== a second booking starts fresh ==");
        FakeResponder again = new FakeResponder(1);
        check("1".equals(assign(manager, three, again)),
                "driver 1 can get a NEW booking after rejecting an old one");

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