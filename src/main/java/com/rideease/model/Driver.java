package com.rideease.model;

import java.time.LocalDateTime;

public class Driver {

    private int driverId;
    private int userId;
    private String licenseNumber;
    private String city;
    private String serviceArea;
    private boolean isOnline;
    private LocalDateTime createdAt;
    private String vehicleType;   // "RICKSHAW" or "CAB" (filled from VEHICLES table later)
    private boolean busy;         // true while the driver has an active ride


    public Driver(int driverId, int userId, String licenseNumber,
                  String city, String serviceArea,
                  boolean isOnline, LocalDateTime createdAt) {

        this.driverId = driverId;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.city = city;
        this.serviceArea = serviceArea;
        this.isOnline = isOnline;
        this.createdAt = createdAt;
    }

    public int getDriverId() {
        return driverId;
    }

    public int getUserId() {
        return userId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getCity() {
        return city;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public boolean isBusy() {
        return busy;
    }


    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}