package com.rideease.dsa.queue;

public class RideQueueDemo {

    public static void main(String[] args) {
        RideQueue queue = new RideQueue(3);

        System.out.println("== EDGE: dequeue/peek on empty queue ==");
        System.out.println("peek on empty:    " + queue.peek());      // null
        System.out.println("dequeue on empty: " + queue.dequeue());   // null
        queue.display();

        System.out.println("\n== enqueue 3 rides (fills the queue) ==");
        queue.enqueue("BK-101: Ali -> Mall Road (Cab)");
        queue.enqueue("BK-102: Sara -> Airport (Rickshaw)");
        queue.enqueue("BK-103: Omar -> Station (Cab)");
        queue.display();

        System.out.println("\n== EDGE: enqueue on full queue ==");
        boolean added = queue.enqueue("BK-999: Extra -> Nowhere (Cab)");
        System.out.println("Added? " + added);                       // false
        queue.display();                                             // unchanged

        System.out.println("\n== peek (no removal) ==");
        System.out.println("Next to serve: " + queue.peek());

        System.out.println("\n== dequeue (serve the oldest) ==");
        System.out.println("Served: " + queue.dequeue());
        queue.display();

        System.out.println("\n== enqueue one more — index WRAPS around ==");
        System.out.println("Added? " + queue.enqueue("BK-104: Hina -> Safari Park (Rickshaw)"));
        queue.display();

        System.out.println("\n== drain the queue ==");
        while (!queue.isEmpty()) {
            System.out.println("Served: " + queue.dequeue());
        }
        queue.display();

        System.out.println("\n== EDGE: dequeue/peek after draining ==");
        System.out.println("peek:    " + queue.peek());              // null
        System.out.println("dequeue: " + queue.dequeue());           // null

        System.out.println("\n== EDGE: invalid capacity ==");
        try {
            new RideQueue(0);
            System.out.println("FAIL: no exception for capacity 0");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught (capacity 0): " + e.getMessage());
        }
        try {
            new RideQueue(-5);
            System.out.println("FAIL: no exception for capacity -5");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught (capacity -5): " + e.getMessage());
        }
    }
}
