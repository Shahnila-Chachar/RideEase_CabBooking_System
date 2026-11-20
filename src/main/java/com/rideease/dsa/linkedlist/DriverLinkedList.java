package com.rideease.dsa.linkedlist;

import com.rideease.model.Driver;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * DriverLinkedList — singly linked list for managing Driver records.
 *
 * Each node holds a full Driver object, so other classes (for example
 * DriverMatchingService) can read city, service area and online status
 * while walking the list.
 *
 * Time complexity:
 *   addFirst()   -> O(1)   new node becomes the head
 *   addLast()    -> O(1)   tail pointer avoids walking the list
 *   removeById() -> O(n)   walks the list to find the id
 *   findById()   -> O(n)   walks the list until the id matches
 *   contains()   -> O(n)   same walk as findById()
 *   iterator()   -> O(1) to create, O(n) to walk the whole list
 *   isEmpty()    -> O(1)
 *   size()       -> O(1)
 *   display()    -> O(n)   visits every node
 *
 * Space: O(n), one node per driver.
 *
 * Driver IDs are ints (Driver.getDriverId()). The list does not check for
 * duplicate IDs; the caller is expected to add each driver once.
 *
 * Because the class implements Iterable, it can be traversed with a
 * for-each loop:
 *     for (Driver d : list) { ... }
 */
public class DriverLinkedList implements Iterable<Driver> {

    // ---- Node ----
    private static class Node {
        Driver driver;
        Node next;

        Node(Driver driver) {
            this.driver = driver;
        }
    }

    private Node head;   // first node
    private Node tail;   // last node
    private int size;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // ---- addFirst: new node becomes the head ----
    public boolean addFirst(Driver driver) {
        if (driver == null) {
            return false;            // EDGE: null driver rejected
        }
        Node newNode = new Node(driver);
        newNode.next = head;
        head = newNode;

        if (tail == null) {
            tail = head;
        }
        size++;
        return true;
    }

    // ---- addLast: new node goes after tail ----
    public boolean addLast(Driver driver) {
        if (driver == null) {
            return false;            // EDGE: null driver rejected
        }
        Node newNode = new Node(driver);
        if (isEmpty()) {
            head = newNode;          // empty list: node is both
            tail = newNode;          // first and last
        } else {
            tail.next = newNode;     // attached after the old last node
            tail = newNode;          // tail moves forward one step
        }
        size++;
        return true;
    }

    // ---- removeById: unlink the first node whose driver id matches ----
    public boolean removeById(int driverId) {
        if (isEmpty()) {
            return false;            // EDGE: nothing to remove
        }

        // EDGE: the head itself matches
        if (head.driver.getDriverId() == driverId) {
            head = head.next;
            if (head == null) {
                tail = null;         // removed the ONLY node
            }
            size--;
            return true;
        }

        // Walk with a prev pointer, looking one node ahead
        Node prev = head;
        while (prev.next != null && prev.next.driver.getDriverId() != driverId) {
            prev = prev.next;
        }

        if (prev.next == null) {
            return false;            // id not found
        }

        if (prev.next == tail) {
            tail = prev;             // removing the LAST node,
        }                            // tail moves back to prev
        prev.next = prev.next.next;  // unlink
        size--;
        return true;
    }

    // ---- findById: return the matching Driver, or null if not found ----
    public Driver findById(int driverId) {
        Node current = head;
        while (current != null) {
            if (current.driver.getDriverId() == driverId) {
                return current.driver;   // found it
            }
            current = current.next;
        }
        return null;                     // reached the end, not found
    }

    // ---- contains: true/false version of findById ----
    public boolean contains(int driverId) {
        return findById(driverId) != null;
    }

    // ---- iterator: lets callers use for-each over the drivers ----
    @Override
    public Iterator<Driver> iterator() {
        return new Iterator<Driver>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Driver next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                Driver driver = current.driver;
                current = current.next;
                return driver;
            }
        };
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("[DriverLinkedList] (empty)");
            return;
        }
        Node current = head;
        System.out.print("HEAD -> ");
        while (current != null) {
            System.out.print(current.driver.getDriverId() + ":" + current.driver.getCity());
            if (current.next != null) {
                System.out.print(" -> ");
            }
            current = current.next;
        }
        System.out.println(" <- TAIL");
    }
}
