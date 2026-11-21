package com.rideease.model;

import java.time.LocalDateTime;

public class Vehicle {
    private int vehicleId;
    private int driverId;
    private String vehicleType;
    private String vehicleNumber;
    private String model;
    private String color;
    private LocalDateTime createdAt;

    public Vehicle(int vehicleId, int driverId, String vehicleType,
                   String vehicleNumber, String model, String color,
                   LocalDateTime createdAt) {
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.model = model;
        this.color = color;
        this.createdAt = createdAt;
    }

    public int getVehicleId() { return vehicleId; }
    public int getDriverId() { return driverId; }
    public String getVehicleType() { return vehicleType; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public void setModel(String model) { this.model = model; }
    public void setColor(String color) { this.color = color; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
