package com.rideease.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int passengerId;
    private Integer driverId;
    private Integer vehicleId;
    private String pickupLocation;
    private String destination;
    private String vehicleType;
    private BigDecimal estimatedFare;
    private BigDecimal finalFare;
    private String status;
    private LocalDateTime createdAt;

    public Booking(int bookingId, int passengerId, Integer driverId,
                   Integer vehicleId, String pickupLocation,
                   String destination, String vehicleType,
                   BigDecimal estimatedFare, BigDecimal finalFare,
                   String status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.vehicleType = vehicleType;
        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getBookingId() { return bookingId; }
    public int getPassengerId() { return passengerId; }
    public Integer getDriverId() { return driverId; }
    public Integer getVehicleId() { return vehicleId; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestination() { return destination; }
    public String getVehicleType() { return vehicleType; }
    public BigDecimal getEstimatedFare() { return estimatedFare; }
    public BigDecimal getFinalFare() { return finalFare; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setEstimatedFare(BigDecimal estimatedFare) { this.estimatedFare = estimatedFare; }
    public void setFinalFare(BigDecimal finalFare) { this.finalFare = finalFare; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
