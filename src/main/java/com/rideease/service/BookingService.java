package com.rideease.service;

import com.rideease.dao.AssignmentAttemptDAO;
import com.rideease.dao.BookingDAO;
import com.rideease.dao.DriverDAO;
import com.rideease.dao.VehicleDAO;
import com.rideease.dsa.linkedlist.DriverLinkedList;
import com.rideease.model.Booking;
import com.rideease.model.Driver;
import com.rideease.model.Vehicle;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final VehicleDAO vehicleDAO;
    private final DriverPoolService driverPoolService;
    private final AssignmentRetryManager retryManager;

    public BookingService(
            BookingDAO bookingDAO,
            DriverDAO driverDAO,
            VehicleDAO vehicleDAO,
            AssignmentAttemptDAO attemptDAO) {

        this.bookingDAO = bookingDAO;
        this.vehicleDAO = vehicleDAO;

        this.driverPoolService = new DriverPoolService(
                driverDAO, vehicleDAO, bookingDAO);

        this.retryManager = new AssignmentRetryManager(
                new DriverMatchingService(), attemptDAO);
    }

    public boolean createAndAssign(
            Booking booking,
            DriverResponder responder) {

        if (booking == null || responder == null) {
            return false;
        }

        if (!isValidLocation(booking.getPickupLocation())
                || !isValidLocation(booking.getDestination())) {
            System.out.println("Invalid pickup or destination.");
            return false;
        }

        booking.setStatus("PENDING");
        booking.setDriverId(null);
        booking.setVehicleId(null);

        if (!bookingDAO.createBooking(booking)) {
            return false;
        }

        DriverLinkedList drivers =
                driverPoolService.loadOnlineDrivers();

        int driverId = retryManager.assignDriver(
                drivers,
                booking.getBookingId(),
                LocationCatalog.CITY,
                LocationCatalog.SERVICE_AREA,
                booking.getVehicleType(),
                responder
        );

        if (driverId == AssignmentRetryManager.NO_DRIVER_AVAILABLE) {
            bookingDAO.updateStatus(
                    booking.getBookingId(),
                    AssignmentRetryManager.NO_DRIVER_AVAILABLE_STATUS);

            System.out.println("No suitable driver available.");
            return false;
        }

        Driver acceptedDriver = drivers.findById(driverId);
        Vehicle vehicle = vehicleDAO.findByDriverId(driverId);

        if (acceptedDriver == null || vehicle == null
                || !bookingDAO.assignDriver(
                        booking.getBookingId(),
                        driverId,
                        vehicle.getVehicleId())) {

            if (acceptedDriver != null) {
                acceptedDriver.setBusy(false);
            }

            bookingDAO.updateStatus(
                    booking.getBookingId(),
                    AssignmentRetryManager.NO_DRIVER_AVAILABLE_STATUS);

            System.out.println("Driver assignment could not be saved.");
            return false;
        }

        booking.setDriverId(driverId);
        booking.setVehicleId(vehicle.getVehicleId());
        booking.setStatus("ACCEPTED");

        System.out.println("Driver successfully assigned!");
        System.out.println("Driver ID: " + driverId);
        System.out.println("Vehicle: " + vehicle.getVehicleType());

        return true;
    }

    private boolean isValidLocation(String locationName) {
        if (locationName == null) {
            return false;
        }

        for (LocationCatalog.Location location : LocationCatalog.getLocations()) {
            if (location.getName().equalsIgnoreCase(locationName.trim())) {
                return true;
            }
        }

        return false;
    }
}
