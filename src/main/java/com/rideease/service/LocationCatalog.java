package com.rideease.service;

import java.util.List;

public class LocationCatalog {

    public static final String CITY = "Sukkur";
    public static final String SERVICE_AREA = "Sukkur";

    public static class Location {
        private final String name;

        public Location(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final List<Location> LOCATIONS = List.of(
        new Location("Sukkur IBA University"),
        new Location("Sukkur Railway Station"),
        new Location("Sukkur Barrage"),
        new Location("Lab-e-Mehran"),
        new Location("Shahi Bazaar"),
        new Location("Sukkur Airport")
    );

    public static List<Location> getLocations() {
        return LOCATIONS;
    }

    public static Location getLocation(int choice) {
        if (choice < 1 || choice > LOCATIONS.size()) {
            return null;
        }
        return LOCATIONS.get(choice - 1);
    }

    public static void displayLocations() {
        for (int i = 0; i < LOCATIONS.size(); i++) {
            System.out.println((i + 1) + ". " + LOCATIONS.get(i).getName());
        }
    }
}
