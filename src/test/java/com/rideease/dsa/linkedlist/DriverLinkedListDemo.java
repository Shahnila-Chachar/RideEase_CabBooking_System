package com.rideease.dsa.linkedlist;

import com.rideease.model.Driver;

import java.time.LocalDateTime;

public class DriverLinkedListDemo {

    // Small helper so the demo stays readable
    private static Driver makeDriver(int id, String city, String area, boolean online) {
        return new Driver(id, id + 100, "LIC-" + id, city, area, online, LocalDateTime.now());
    }

    public static void main(String[] args) {
        DriverLinkedList list = new DriverLinkedList();

        Driver ali  = makeDriver(1, "Sukkur", "Military Road", true);
        Driver sara = makeDriver(2, "Sukkur", "Barrage Road", true);
        Driver hina = makeDriver(3, "Karachi", "Gulshan", false);
        Driver omar = makeDriver(4, "Larkana", "Station Road", true);
        Driver zain = makeDriver(5, "Sukkur", "Airport Road", true);

        System.out.println("== EDGE: operations on an empty list ==");
        System.out.println("findById on empty: " + list.findById(1));        // null
        System.out.println("contains on empty: " + list.contains(1));        // false
        list.display();
        System.out.println("size: " + list.size());
        System.out.println("removeById on empty: " + list.removeById(1));    // false

        System.out.println("\n== EDGE: null input ==");
        System.out.println("addFirst null: " + list.addFirst(null));         // false
        System.out.println("addLast null:  " + list.addLast(null));          // false
        System.out.println("size: " + list.size());                          // 0

        System.out.println("\n== addFirst: Ali, then Sara ==");
        System.out.println("Added Ali?  " + list.addFirst(ali));
        System.out.println("Added Sara? " + list.addFirst(sara));            // Sara lands IN FRONT of Ali
        list.display();
        System.out.println("size: " + list.size());

        System.out.println("\n== addLast(Hina) ==");
        System.out.println("Added Hina? " + list.addLast(hina));
        list.display();                                                      // Sara -> Ali -> Hina
        System.out.println("size: " + list.size());

        System.out.println("\n== for-each traversal (Iterable) ==");
        for (Driver d : list) {
            System.out.println("  Driver " + d.getDriverId() + " online=" + d.isOnline());
        }

        System.out.println("\n== EDGE: remove a MIDDLE node (Ali, id 1) ==");
        System.out.println("Removed Ali? " + list.removeById(1));
        list.display();                                                      // Sara -> Hina
        System.out.println("size: " + list.size());
        list.addFirst(ali);                                                  // put Ali back at the front
        list.display();                                                      // Ali -> Sara -> Hina

        System.out.println("\n== EDGE: remove an id that doesn't exist ==");
        System.out.println("Removed id 99? " + list.removeById(99));         // false
        list.display();
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: remove the tail (Hina), then addLast(Omar) ==");
        System.out.println("Removed Hina? " + list.removeById(3));
        list.addLast(omar);
        list.display();                                                      // Ali -> Sara -> Omar
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: remove the head (Ali) ==");
        System.out.println("Removed Ali? " + list.removeById(1));
        list.display();                                                      // Sara -> Omar
        System.out.println("size: " + list.size());

        System.out.println("\n== findById / contains ==");
        Driver found = list.findById(2);
        System.out.println("find id 2: " + (found == null ? "null" : found.getServiceArea())); // Barrage Road
        System.out.println("find id 4 (tail): " + list.contains(4));        // true
        System.out.println("find id 99: " + list.contains(99));             // false

        System.out.println("\n== drain: remove Sara, then Omar ==");
        System.out.println("Removed Sara? " + list.removeById(2));
        list.display();
        System.out.println("Removed Omar? " + list.removeById(4));
        list.display();                                                      // (empty)
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: addLast(Zain) on a fully drained list ==");
        System.out.println("Added Zain? " + list.addLast(zain));
        list.display();
        System.out.println("size: " + list.size());
    }
}
