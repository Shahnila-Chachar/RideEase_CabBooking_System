package com.rideease.dsa.queue;

/**
 * RideQueue — circular array-based FIFO queue for pending ride requests.
 *
 * Time complexity:
 *   enqueue()  -> O(1)   advance rear pointer + insert
 *   dequeue()  -> O(1)   advance front pointer + return (no shifting —
 *                         the circular index makes this possible)
 *   peek()     -> O(1)   direct array access
 *   isEmpty()  -> O(1)   single comparison
 *   display()  -> O(n)   n = number of live ride requests
 *
 * Space: O(capacity), fixed for the lifetime of the queue.
 *
 * Note: Day 1 stores String labels because the Ride model class doesn't
 * exist yet. Future upgrade:will make this generic (RideQueue<T>) so it can
 * hold Ride objects.
 */
public class RideQueue {

    private String[] rides;     // underlying storage
    private int front;          // index of the next request to be served
    private int rear;           // index of the most recently added request
    private int size;           // how many requests are currently stored
    private int capacity;       // maximum number of requests

    public RideQueue(int capacity) {
        this.capacity = capacity;
        this.rides = new String[capacity];
        this.front = 0;
        this.rear = -1;         // becomes 0 on the first enqueue
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public void enqueue(String ride) {
        if (isFull()) {
            System.out.println("[RideQueue] Full — rejected: " + ride);
            return;
        }
        rear = (rear + 1) % capacity;   // circular wrap-around
        rides[rear] = ride;
        size++;
    }

    public String dequeue() {
        if (isEmpty()) {
            System.out.println("[RideQueue] Empty — nothing to serve");
            return null;
        }
        String served = rides[front];   // FIFO: front holds the oldest request
        rides[front] = null;            // clear the slot (GC + easier debugging)
        front = (front + 1) % capacity; // circular wrap-around
        size--;
        return served;
    }

    public String peek() {
        if (isEmpty()) {
            return null;
        }
        return rides[front];
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("[RideQueue] (empty)");
            return;
        }
        System.out.print("FRONT -> ");
        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;   // logical order, handles wrap
            System.out.print(rides[index]);
            if (i < size - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println(" <- REAR");
    }

    public static void main(String[] args) {
        RideQueue queue = new RideQueue(3);

        System.out.println("== enqueue 3 rides (fills the queue) ==");
        queue.enqueue("BK-101: Ali -> Mall Road (Cab)");
        queue.enqueue("BK-102: Sara -> Airport (Rickshaw)");
        queue.enqueue("BK-103: Omar -> Station (Cab)");
        queue.display();

        System.out.println("\n== peek (no removal) ==");
        System.out.println("Next to serve: " + queue.peek());

        System.out.println("\n== dequeue (serve the oldest) ==");
        System.out.println("Served: " + queue.dequeue());
        queue.display();

        System.out.println("\n== enqueue one more — index WRAPS around ==");
        queue.enqueue("BK-104: Hina -> Safari Park (Rickshaw)");
        queue.display();

        System.out.println("\n== drain the queue ==");
        while (!queue.isEmpty()) {
            System.out.println("Served: " + queue.dequeue());
        }
        queue.display();
    }
}