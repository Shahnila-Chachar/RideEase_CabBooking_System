package com.rideease.dsa.linkedlist;

public class DriverLinkedListDemo {
    public static void main(String[] args) {
        DriverLinkedList list = new DriverLinkedList();


        System.out.println("== EDGE: operations on an empty list ==");
        System.out.println("findById on empty: " + list.findById("Anyone")); // false
        list.display();
        System.out.println("size: " + list.size());
        System.out.println("removeById on empty: " + list.removeById("Anyone"));

        System.out.println("\n== EDGE: null input ==");
        System.out.println("addFirst null: " + list.addFirst(null));       // false
        System.out.println("addLast null:  " + list.addLast(null));        // false
        System.out.println("removeById null: " + list.removeById(null));   // false
        System.out.println("size: " + list.size());                       // 0

        System.out.println("\n== addFirst: Ali, then Sara ==");
        System.out.println("Added Ali?  " + list.addFirst("Ali"));
        System.out.println("Added Sara? " + list.addFirst("Sara"));        // Sara lands IN FRONT of Ali
        list.display();
        System.out.println("size: " + list.size());

        System.out.println("\n== THE HINA TEST: addLast(\"Hina\") ==");
        System.out.println("Added Hina? " + list.addLast("Hina"));
        list.display();
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: remove a MIDDLE node (Ali) ==");
        System.out.println("Removed Ali? " + list.removeById("Ali"));
        list.display();                                                   // Sara -> Hina
        System.out.println("size: " + list.size());
        list.addFirst("Ali");                                             // put Ali back at the front
        list.display();                                                   // Ali -> Sara -> Hina

        System.out.println("\n== EDGE: remove an id that doesn't exist ==");
        System.out.println("Removed Nobody? " + list.removeById("Nobody")); // false
        list.display();
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: remove the tail (Hina), then addLast(\"Omar\") ==");
        System.out.println("Removed Hina? " + list.removeById("Hina"));
        list.addLast("Omar");
        list.display();                                                   // Ali -> Sara -> Omar
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: remove the head (Ali) ==");
        System.out.println("Removed Ali? " + list.removeById("Ali"));
        list.display();                                                   // Sara -> Omar
        System.out.println("size: " + list.size());

        System.out.println("\n== findById ==");
        System.out.println("find Sara:   " + list.findById("Sara"));     // true
        System.out.println("find Omar:   " + list.findById("Omar"));     // true (the tail)
        System.out.println("find Nobody: " + list.findById("Nobody"));   // false
        System.out.println("find null:   " + list.findById(null));       // false

        System.out.println("\n== drain: remove Sara, then Omar ==");
        System.out.println("Removed Sara? " + list.removeById("Sara"));
        list.display();
        System.out.println("Removed Omar? " + list.removeById("Omar"));
        list.display();                                                   // (empty)
        System.out.println("size: " + list.size());

        System.out.println("\n== EDGE: addLast(\"Zain\") on a fully drained list ==");
        System.out.println("Added Zain? " + list.addLast("Zain"));
        list.display();
        System.out.println("size: " + list.size());
    }
}