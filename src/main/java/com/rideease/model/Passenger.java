
package com.rideease.model;

import java.time.LocalDateTime;

public class Passenger {

    private int passengerId;
    private int userId;
    private LocalDateTime createdAt;

    // Constructor
    public Passenger(int passengerId, int userId, LocalDateTime createdAt) {
        this.passengerId = passengerId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getters
    public int getPassengerId() {
        return passengerId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
