package com.rideease.dsa.linkedlist;

/**
 * DriverLinkedList — singly linked list for managing driver records.
 *
 * Time complexity:
 *   addFirst()   -> O(1)   new node becomes the head
 *   addLast()    -> O(1)   tail pointer avoids walking the list
 *   removeById() -> O(n)   walks the list to find the id
 *   isEmpty()    -> O(1)
 *   size()       -> O(1)
 *   display()    -> O(n)   visits every node
 *
 * Space: O(n), one node per driver.
 *
 * Note: DriverLinkedList stores String driver IDs because the Driver model class
 * doesn't exist yet. In future nodes will hold Driver objects.
 */
public class DriverLinkedList {

    // ---- Node ----
    private static class Node {
        String driverId;
        Node next;

        Node(String driverId) {
            this.driverId = driverId;
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
    public boolean addFirst(String driverId) {
        if (driverId == null) {
            return false;            // EDGE: null id rejected
        }
        Node newNode = new Node(driverId);
        newNode.next = head;
        head = newNode;

        if (tail == null) {
            tail = head;
        }
        size++;
        return true;
    }

    // ---- addLast: new node goes after tail ----
    public boolean addLast(String driverId) {
        if (driverId == null) {
            return false;            // EDGE: null id rejected
        }
        Node newNode = new Node(driverId);
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

    // ---- removeById: unlink the first node whose id matches ----
    public boolean removeById(String driverId) {
        if (driverId == null || isEmpty()) {
            return false;            // EDGE: null id or nothing to remove
        }

        // EDGE: the head itself matches
        if (head.driverId.equals(driverId)) {
            head = head.next;
            if (head == null) {
                tail = null;         // removed the ONLY node
            }
            size--;
            return true;
        }

        // Walk with a prev pointer, looking one node ahead
        Node prev = head;
        while (prev.next != null && !prev.next.driverId.equals(driverId)) {
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

    public void display() {
        if (isEmpty()) {
            System.out.println("[DriverLinkedList] (empty)");
            return;
        }
        Node current = head;
        System.out.print("HEAD -> ");
        while (current != null) {
            System.out.print(current.driverId);
            if (current.next != null) {
                System.out.print(" -> ");
            }
            current = current.next;
        }
        System.out.println(" <- TAIL");
    }
}