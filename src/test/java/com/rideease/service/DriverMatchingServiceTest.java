package com.rideease.service;

import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Driver;

import java.time.LocalDateTime;

public class DriverMatchingServiceTest {

    private static int passed = 0;

    // Builds a driver that is eligible for (Sukkur, Military Road, CAB)
    private static Driver makeDriver(int id) {
        Driver d = new Driver(id, id + 100, "LIC-" + id, "Sukkur",
                "Military Road", true, LocalDateTime.now());
        d.setVehicleType("CAB");
        d.setBusy(false);
        return d;
    }

    private static Driver find(DriverMatchingService service,
                               DriverLinkedList drivers,
                               DriverLinkedList excluded) {
        return service.findEligibleDriver(
                drivers, excluded, "Sukkur", "Military Road", "CAB");
    }

    public static void main(String[] args) {
        DriverMatchingService service = new DriverMatchingService();

        System.out.println("== EDGE: empty and null input ==");
        check(find(service, new DriverLinkedList(), null) == null,
                "empty list returns null");
        check(find(service, null, null) == null,
                "null list returns null");

        System.out.println("\n== one eligible driver ==");
        DriverLinkedList one = new DriverLinkedList();
        one.addLast(makeDriver(1));
        Driver found = find(service, one, null);
        check(found != null && found.getDriverId() == 1,
                "eligible driver is returned");
        check(find(service, one, new DriverLinkedList()) != null,
                "empty excluded list changes nothing");

        System.out.println("\n== each rule skips a driver ==");
        Driver offline = makeDriver(2);
        offline.setOnline(false);
        check(findOnly(service, offline) == null, "offline driver skipped");

        Driver busy = makeDriver(3);
        busy.setBusy(true);
        check(findOnly(service, busy) == null, "busy driver skipped");

        Driver wrongCity = makeDriver(4);
        wrongCity.setCity("Karachi");
        check(findOnly(service, wrongCity) == null, "wrong city skipped");

        Driver wrongArea = makeDriver(5);
        wrongArea.setServiceArea("Barrage Road");
        check(findOnly(service, wrongArea) == null, "wrong service area skipped");

        Driver wrongVehicle = makeDriver(6);
        wrongVehicle.setVehicleType("RICKSHAW");
        check(findOnly(service, wrongVehicle) == null, "wrong vehicle type skipped");

        Driver noVehicle = makeDriver(7);
        noVehicle.setVehicleType(null);
        check(findOnly(service, noVehicle) == null, "driver with no vehicle type skipped");

        Driver noCity = makeDriver(8);
        noCity.setCity(null);
        check(findOnly(service, noCity) == null, "driver with no city skipped");

        System.out.println("\n== text matching ignores case ==");
        Driver lower = makeDriver(9);
        lower.setCity("sukkur");
        lower.setVehicleType("cab");
        check(findOnly(service, lower) != null, "case is ignored");

        System.out.println("\n== extra spaces are ignored ==");
        Driver spaced = makeDriver(12);
        spaced.setCity("  Sukkur ");
        spaced.setServiceArea(" Military Road  ");
        spaced.setVehicleType(" CAB ");
        check(findOnly(service, spaced) != null, "leading/trailing spaces ignored");
        check(service.findEligibleDriver(one, null, " Sukkur", "Military Road ", " cab") != null,
                "spaces in the booking values are ignored too");

        System.out.println("\n== null driver cannot enter the list ==");
        DriverLinkedList withNull = new DriverLinkedList();
        check(!withNull.addLast(null), "addLast(null) is rejected");
        withNull.addLast(makeDriver(13));
        Driver afterNull = find(service, withNull, null);
        check(afterNull != null && afterNull.getDriverId() == 13,
                "list still works after a rejected null");

        System.out.println("\n== one eligible driver among several ==");
        DriverLinkedList mixed = new DriverLinkedList();
        mixed.addLast(offline);          // skipped
        mixed.addLast(busy);             // skipped
        mixed.addLast(wrongCity);        // skipped
        mixed.addLast(makeDriver(10));   // the only eligible one
        mixed.addLast(makeDriver(11));   // eligible, but comes later
        Driver pick = find(service, mixed, null);
        check(pick != null && pick.getDriverId() == 10,
                "first eligible driver is returned");

        System.out.println("\n== excluded drivers are skipped ==");
        DriverLinkedList excluded = new DriverLinkedList();
        excluded.addLast(makeDriver(10));
        Driver next = find(service, mixed, excluded);
        check(next != null && next.getDriverId() == 11,
                "excluded driver 10 skipped, 11 returned");
        excluded.addLast(makeDriver(11));
        check(find(service, mixed, excluded) == null,
                "all eligible drivers excluded returns null");

        System.out.println("\n== duplicate driver ids ==");
        DriverLinkedList dupes = new DriverLinkedList();
        dupes.addLast(makeDriver(20));
        dupes.addLast(makeDriver(20));   // same id twice
        DriverLinkedList excludeTwenty = new DriverLinkedList();
        excludeTwenty.addLast(makeDriver(20));
        check(find(service, dupes, null) != null, "duplicate ids: first copy is found");
        check(find(service, dupes, excludeTwenty) == null,
                "duplicate ids: excluding id 20 skips both copies");

        System.out.println("\nAll " + passed + " checks passed.");
    }

    // list containing just one driver
    private static Driver findOnly(DriverMatchingService service, Driver driver) {
        DriverLinkedList list = new DriverLinkedList();
        list.addLast(driver);
        return find(service, list, null);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Test failed: " + message);
        }
        passed++;
        System.out.println("PASS: " + message);
    }
}