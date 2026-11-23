package com.rideease.service;

import com.rideease.dao.BookingDAO;
import com.rideease.dao.DriverDAO;
import com.rideease.dao.VehicleDAO;
import com.rideease.dsa.linkedlist.DriverLinkedList;

public class DriverPoolService {

    private final DriverDAO driverDAO;
    private final DriverPoolLoader driverPoolLoader;

    public DriverPoolService(
            DriverDAO driverDAO,
            VehicleDAO vehicleDAO,
            BookingDAO bookingDAO) {
        this.driverDAO = driverDAO;
        this.driverPoolLoader = new DriverPoolLoader(vehicleDAO, bookingDAO);
    }

    public DriverLinkedList loadOnlineDrivers() {
        return driverPoolLoader.loadDrivers(driverDAO.findOnlineDrivers());
    }
}
