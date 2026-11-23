package com.rideease.model;

import java.time.LocalDateTime;

/**
 * RideAssignmentAttempt — one row of RIDE_ASSIGNMENT_ATTEMPTS.
 *
 * Records that a booking was offered to a driver and how the driver answered:
 *   PENDING  — offered, no answer recorded yet
 *   ACCEPTED — driver took the booking
 *   REJECTED — driver said no (the retry loop moves to the next driver)
 *   EXPIRED  — offer was never answered (kept for completeness)
 */
public class RideAssignmentAttempt {

    private int attemptId;
    private int bookingId;
    private int driverId;
    private String attemptStatus;
    private LocalDateTime attemptedAt;
    private LocalDateTime respondedAt;

    public RideAssignmentAttempt(int bookingId, int driverId) {
        this.bookingId = bookingId;
        this.driverId = driverId;
        this.attemptStatus = "PENDING";
    }

    public RideAssignmentAttempt(int attemptId, int bookingId, int driverId,
                                 String attemptStatus,
                                 LocalDateTime attemptedAt,
                                 LocalDateTime respondedAt) {
        this.attemptId = attemptId;
        this.bookingId = bookingId;
        this.driverId = driverId;
        this.attemptStatus = attemptStatus;
        this.attemptedAt = attemptedAt;
        this.respondedAt = respondedAt;
    }

    public int getAttemptId() { return attemptId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getAttemptStatus() { return attemptStatus; }
    public void setAttemptStatus(String attemptStatus) { this.attemptStatus = attemptStatus; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}