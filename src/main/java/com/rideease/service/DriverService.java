package com.rideease.service;

import com.rideease.model.Driver;
import com.rideease.validation.InputValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DriverService {

    private final Map<Integer, Driver> driversById = new HashMap<>();
    private final Map<Integer, Driver> driversByUserId = new HashMap<>();
    private final Map<Integer, String> driverVehicleTypes = new HashMap<>();
    private final Set<Integer> busyDriverIds = new HashSet<>();
    private int nextDriverId = 1;

    public Driver registerDriverProfile(int userId, String licenseNumber, String city, String serviceArea, String vehicleType) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        if (driversByUserId.containsKey(userId)) {
            throw new IllegalArgumentException("Driver profile already exists for this user.");
        }
        if (InputValidator.isEmptyOrBlank(licenseNumber)) {
            throw new IllegalArgumentException("License number cannot be empty.");
        }
        if (InputValidator.isEmptyOrBlank(city)) {
            throw new IllegalArgumentException("City cannot be empty.");
        }
        if (InputValidator.isEmptyOrBlank(serviceArea)) {
            throw new IllegalArgumentException("Service area cannot be empty.");
        }
        if (InputValidator.isEmptyOrBlank(vehicleType)) {
            throw new IllegalArgumentException("Vehicle type cannot be empty.");
        }

        Driver driver = new Driver(
                nextDriverId++,
                userId,
                licenseNumber.trim(),
                city.trim(),
                serviceArea.trim(),
                false,
                LocalDateTime.now()
        );

        driversById.put(driver.getDriverId(), driver);
        driversByUserId.put(userId, driver);
        driverVehicleTypes.put(driver.getDriverId(), vehicleType.trim().toUpperCase());
        return driver;
    }

    public void addExistingDriver(Driver driver, String vehicleType) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null.");
        }
        driversById.put(driver.getDriverId(), driver);
        driversByUserId.put(driver.getUserId(), driver);
        if (!InputValidator.isEmptyOrBlank(vehicleType)) {
            driverVehicleTypes.put(driver.getDriverId(), vehicleType.trim().toUpperCase());
        }
        if (driver.getDriverId() >= nextDriverId) {
            nextDriverId = driver.getDriverId() + 1;
        }
    }

    public Driver getDriverById(int driverId) {
        return driversById.get(driverId);
    }

    public Driver getDriverByUserId(int userId) {
        return driversByUserId.get(userId);
    }

    public String getVehicleTypeForDriver(int driverId) {
        return driverVehicleTypes.get(driverId);
    }

    public void setOnlineStatus(int driverId, boolean isOnline) {
        Driver driver = requireDriver(driverId);
        if (!isOnline && busyDriverIds.contains(driverId)) {
            throw new IllegalStateException("Cannot go offline while on an active ride.");
        }
        driver.setOnline(isOnline);
    }

    public void updateLocation(int driverId, String city, String serviceArea) {
        Driver driver = requireDriver(driverId);
        if (InputValidator.isEmptyOrBlank(city) || InputValidator.isEmptyOrBlank(serviceArea)) {
            throw new IllegalArgumentException("City and service area cannot be empty.");
        }
        driver.setCity(city.trim());
        driver.setServiceArea(serviceArea.trim());
    }

    public boolean isDriverAvailable(int driverId) {
        Driver driver = driversById.get(driverId);
        return driver != null && driver.isOnline() && !busyDriverIds.contains(driverId);
    }

    public void markDriverBusy(int driverId) {
        requireDriver(driverId);
        if (busyDriverIds.contains(driverId)) {
            throw new IllegalStateException("Driver already has an active ride.");
        }
        busyDriverIds.add(driverId);
    }

    public void markDriverAvailable(int driverId) {
        requireDriver(driverId);
        busyDriverIds.remove(driverId);
    }

    public Driver findMatchingDriver(String city, String serviceArea, String vehicleType, Set<Integer> rejectedDriverIds) {
        if (InputValidator.isEmptyOrBlank(city) || InputValidator.isEmptyOrBlank(serviceArea) || InputValidator.isEmptyOrBlank(vehicleType)) {
            return null;
        }

        String targetCity = city.trim();
        String targetArea = serviceArea.trim();
        String targetVehicle = vehicleType.trim().toUpperCase();

        for (Driver driver : driversById.values()) {
            int driverId = driver.getDriverId();
            if (!driver.isOnline()) {
                continue;
            }
            if (busyDriverIds.contains(driverId)) {
                continue;
            }
            if (rejectedDriverIds != null && rejectedDriverIds.contains(driverId)) {
                continue;
            }
            if (!targetCity.equalsIgnoreCase(driver.getCity())) {
                continue;
            }
            if (!targetArea.equalsIgnoreCase(driver.getServiceArea())) {
                continue;
            }
            String assignedVehicle = driverVehicleTypes.get(driverId);
            if (assignedVehicle != null && targetVehicle.equalsIgnoreCase(assignedVehicle)) {
                return driver;
            }
        }
        return null;
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(driversById.values());
    }

    private Driver requireDriver(int driverId) {
        Driver driver = driversById.get(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found with ID: " + driverId);
        }
        return driver;
    }
}