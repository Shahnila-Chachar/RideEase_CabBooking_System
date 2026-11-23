package com.rideease.service;

import java.util.HashMap;
import java.util.Map;

public class FareService {

    private static final double MIN_FARE = 100.0;
    private final Map<String, Double> baseRates = new HashMap<>();
    private final Map<String, Double> perKmRates = new HashMap<>();

    public FareService() {
        baseRates.put("BIKE", 50.0);
        perKmRates.put("BIKE", 20.0);

        baseRates.put("AUTO", 80.0);
        perKmRates.put("AUTO", 30.0);

        baseRates.put("MINI", 120.0);
        perKmRates.put("MINI", 45.0);

        baseRates.put("SEDAN", 160.0);
        perKmRates.put("SEDAN", 60.0);

        baseRates.put("SUV", 220.0);
        perKmRates.put("SUV", 80.0);
    }

    public double calculateFare(double distanceKm, String vehicleType) {
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0 km.");
        }
        String normalizedType = normalizeVehicleType(vehicleType);
        double base = baseRates.get(normalizedType);
        double perKm = perKmRates.get(normalizedType);
        double total = base + (distanceKm * perKm);
        return Math.round(Math.max(total, MIN_FARE) * 100.0) / 100.0;
    }

    public boolean isSupportedVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            return false;
        }
        return baseRates.containsKey(vehicleType.trim().toUpperCase());
    }

    private String normalizeVehicleType(String vehicleType) {
        if (!isSupportedVehicleType(vehicleType)) {
            throw new IllegalArgumentException("Unsupported vehicle type: " + vehicleType);
        }
        return vehicleType.trim().toUpperCase();
    }
}