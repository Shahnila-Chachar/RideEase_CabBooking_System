package com.rideease.service;

import com.rideease.dsa.graph.Graph;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareService {
    private final Graph graph = new Graph();

    private static final double RICKSHAW_BASE_FARE = 50.0;
    private static final double RICKSHAW_PER_KM = 25.0;
    private static final double CAB_BASE_FARE = 100.0;
    private static final double CAB_PER_KM = 45.0;

    public FareService() {
        String iba = "Sukkur IBA University";
        String station = "Sukkur Railway Station";
        String barrage = "Sukkur Barrage";
        String mehran = "Lab-e-Mehran";
        String bazaar = "Shahi Bazaar";
        String airport = "Sukkur Airport";

        graph.addRoad(iba, station, 5.0);
        graph.addRoad(iba, barrage, 7.0);
        graph.addRoad(station, bazaar, 2.0);
        graph.addRoad(bazaar, barrage, 3.0);
        graph.addRoad(barrage, mehran, 4.0);
        graph.addRoad(station, mehran, 5.0);
        graph.addRoad(iba, airport, 8.0);
        graph.addRoad(airport, station, 6.0);
        graph.addRoad(bazaar, mehran, 2.0);
    }

    public Graph.Route calculateRoute(String pickup, String destination) {
        return graph.shortestPath(pickup, destination);
    }

    public BigDecimal calculateFare(String pickup, String destination,
                                    String vehicleType) {
        Graph.Route route = calculateRoute(pickup, destination);

        if (!route.isAvailable()) {
            throw new IllegalArgumentException("Route is not available.");
        }

        double baseFare;
        double perKm;

        if ("RICKSHAW".equalsIgnoreCase(vehicleType)) {
            baseFare = RICKSHAW_BASE_FARE;
            perKm = RICKSHAW_PER_KM;
        } else if ("CAB".equalsIgnoreCase(vehicleType)) {
            baseFare = CAB_BASE_FARE;
            perKm = CAB_PER_KM;
        } else {
            throw new IllegalArgumentException("Invalid vehicle type.");
        }

        double fare = baseFare + route.getDistance() * perKm;

        return BigDecimal.valueOf(fare).setScale(2, RoundingMode.HALF_UP);
    }
}