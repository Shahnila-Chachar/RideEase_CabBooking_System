package com.rideease.service;

import com.rideease.dao.BookingDAO;
import com.rideease.dao.VehicleDAO;
import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Booking;
import com.rideease.model.Driver;
import com.rideease.model.Vehicle;

import java.util.List;

public class DriverPoolLoader {

    private final VehicleDAO vehicleDAO;
    private final BookingDAO bookingDAO;

    public DriverPoolLoader(VehicleDAO vehicleDAO, BookingDAO bookingDAO) {
        this.vehicleDAO = vehicleDAO;
        this.bookingDAO = bookingDAO;
    }

    public DriverLinkedList loadDrivers(List<Driver> onlineDrivers) {
        DriverLinkedList drivers = new DriverLinkedList();

        if (onlineDrivers == null) {
            return drivers;
        }

        for (Driver driver : onlineDrivers) {
            if (driver == null) {
                continue;
            }

            Vehicle vehicle = vehicleDAO.findByDriverId(driver.getDriverId());

            if (vehicle == null) {
                continue;
            }

            driver.setVehicleType(vehicle.getVehicleType());
            driver.setBusy(hasActiveRide(driver.getDriverId()));
            drivers.addLast(driver);
        }

        return drivers;
    }

    private boolean hasActiveRide(int driverId) {
        List<Booking> bookings = bookingDAO.findByDriverId(driverId);

        for (Booking booking : bookings) {
            String status = booking.getStatus();

            if ("ACCEPTED".equals(status) || "IN_PROGRESS".equals(status)) {
                return true;
            }
        }

        return false;
    }
}
