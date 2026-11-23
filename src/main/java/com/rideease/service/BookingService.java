package com.rideease.service;

import com.rideease.model.Driver;
import com.rideease.validation.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookingService {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_NO_DRIVER_AVAILABLE = "NO_DRIVER_AVAILABLE";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public static class RideRecord {
        private int bookingId;
        private int passengerId;
        private Integer driverId;
        private Integer vehicleId;
        private String city;
        private String serviceArea;
        private String pickupLocation;
        private String destination;
        private String vehicleType;
        private double distanceKm;
        private BigDecimal estimatedFare;
        private BigDecimal finalFare;
        private String status;
        private boolean paid;
        private String paymentMethod;
        private LocalDateTime createdAt;
        private final Set<Integer> rejectedDriverIds = new HashSet<>();

        public int getBookingId() {
            return bookingId;
        }

        public void setBookingId(int bookingId) {
            this.bookingId = bookingId;
        }

        public int getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(int passengerId) {
            this.passengerId = passengerId;
        }

        public Integer getDriverId() {
            return driverId;
        }

        public void setDriverId(Integer driverId) {
            this.driverId = driverId;
        }

        public Integer getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(Integer vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getServiceArea() {
            return serviceArea;
        }

        public void setServiceArea(String serviceArea) {
            this.serviceArea = serviceArea;
        }

        public String getPickupLocation() {
            return pickupLocation;
        }

        public void setPickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public void setDistanceKm(double distanceKm) {
            this.distanceKm = distanceKm;
        }

        public BigDecimal getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(BigDecimal estimatedFare) {
            this.estimatedFare = estimatedFare;
        }

        public BigDecimal getFinalFare() {
            return finalFare;
        }

        public void setFinalFare(BigDecimal finalFare) {
            this.finalFare = finalFare;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public Set<Integer> getRejectedDriverIds() {
            return rejectedDriverIds;
        }
    }

    private final DriverService driverService;
    private final FareService fareService;
    private final Map<Integer, RideRecord> bookings = new LinkedHashMap<>();
    private final Map<Integer, Integer> activeBookingByDriver = new HashMap<>();
    private int nextBookingId = 1;

    public BookingService(DriverService driverService, FareService fareService) {
        if (driverService == null || fareService == null) {
            throw new IllegalArgumentException("DriverService and FareService cannot be null.");
        }
        this.driverService = driverService;
        this.fareService = fareService;
    }

    public RideRecord createBooking(int passengerId,
                                    String city,
                                    String serviceArea,
                                    String pickupLocation,
                                    String destination,
                                    String vehicleType,
                                    double distanceKm) {
        if (passengerId <= 0) {
            throw new IllegalArgumentException("Invalid passenger ID.");
        }
        if (InputValidator.isEmptyOrBlank(city)) {
            throw new IllegalArgumentException("City cannot be empty.");
        }
        if (InputValidator.isEmptyOrBlank(serviceArea)) {
            throw new IllegalArgumentException("Service area cannot be empty.");
        }
        if (InputValidator.isEmptyOrBlank(pickupLocation)) {
            throw new IllegalArgumentException("Pickup location cannot be empty.");
        }
        if (InputValidator.isEmptyOrBlank(destination)) {
            throw new IllegalArgumentException("Destination cannot be empty.");
        }
        if (!fareService.isSupportedVehicleType(vehicleType)) {
            throw new IllegalArgumentException("Unsupported vehicle type.");
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0.");
        }

        double fareValue = fareService.calculateFare(distanceKm, vehicleType);
        BigDecimal estimatedFare = BigDecimal.valueOf(fareValue);

        RideRecord ride = new RideRecord();
        ride.setBookingId(nextBookingId++);
        ride.setPassengerId(passengerId);
        ride.setCity(city.trim());
        ride.setServiceArea(serviceArea.trim());
        ride.setPickupLocation(pickupLocation.trim());
        ride.setDestination(destination.trim());
        ride.setVehicleType(vehicleType.trim().toUpperCase());
        ride.setDistanceKm(distanceKm);
        ride.setEstimatedFare(estimatedFare);
        ride.setStatus(STATUS_PENDING);
        ride.setCreatedAt(LocalDateTime.now());

        bookings.put(ride.getBookingId(), ride);
        assignNextAvailableDriver(ride);
        return ride;
    }

    public boolean assignNextAvailableDriver(RideRecord ride) {
        if (ride == null) {
            return false;
        }
        Driver match = driverService.findMatchingDriver(
                ride.getCity(),
                ride.getServiceArea(),
                ride.getVehicleType(),
                ride.getRejectedDriverIds()
        );
        if (match == null) {
            ride.setDriverId(null);
            ride.setStatus(STATUS_NO_DRIVER_AVAILABLE);
            return false;
        }
        ride.setDriverId(match.getDriverId());
        ride.setStatus(STATUS_PENDING);
        return true;
    }

    public boolean acceptBooking(int bookingId, int driverId) {
        RideRecord ride = requireBooking(bookingId);
        if (!STATUS_PENDING.equals(ride.getStatus())) {
            throw new IllegalStateException("Only PENDING bookings can be accepted.");
        }
        if (ride.getDriverId() == null || ride.getDriverId() != driverId) {
            throw new IllegalArgumentException("This booking is not assigned to driver ID " + driverId);
        }
        if (activeBookingByDriver.containsKey(driverId)) {
            throw new IllegalStateException("Driver already has an active ride.");
        }

        driverService.markDriverBusy(driverId);
        activeBookingByDriver.put(driverId, bookingId);
        ride.setStatus(STATUS_ACCEPTED);
        return true;
    }

    public boolean rejectBooking(int bookingId, int driverId) {
        RideRecord ride = requireBooking(bookingId);
        if (!STATUS_PENDING.equals(ride.getStatus())) {
            throw new IllegalStateException("Only PENDING bookings can be rejected.");
        }
        if (ride.getDriverId() == null || ride.getDriverId() != driverId) {
            throw new IllegalArgumentException("This booking is not assigned to driver ID " + driverId);
        }

        ride.getRejectedDriverIds().add(driverId);
        ride.setDriverId(null);
        return assignNextAvailableDriver(ride);
    }

    public boolean cancelBooking(int bookingId, int passengerId) {
        RideRecord ride = requireBooking(bookingId);
        if (ride.getPassengerId() != passengerId) {
            throw new IllegalArgumentException("Passenger can only cancel their own bookings.");
        }
        if (!STATUS_PENDING.equals(ride.getStatus())) {
            throw new IllegalStateException("Only PENDING bookings can be cancelled.");
        }

        ride.setDriverId(null);
        ride.setStatus(STATUS_CANCELLED);
        return true;
    }

    public boolean completeBooking(int bookingId, int driverId) {
        RideRecord ride = requireBooking(bookingId);
        if (!STATUS_ACCEPTED.equals(ride.getStatus())) {
            throw new IllegalStateException("Only ACCEPTED bookings can be completed.");
        }
        if (ride.getDriverId() == null || ride.getDriverId() != driverId) {
            throw new IllegalArgumentException("Only the assigned driver can complete this booking.");
        }

        ride.setStatus(STATUS_COMPLETED);
        ride.setFinalFare(ride.getEstimatedFare());
        activeBookingByDriver.remove(driverId);
        driverService.markDriverAvailable(driverId);
        return true;
    }

    public boolean processPayment(int bookingId, int passengerId, String paymentMethod) {
        RideRecord ride = requireBooking(bookingId);
        if (ride.getPassengerId() != passengerId) {
            throw new IllegalArgumentException("Passenger can only pay for their own bookings.");
        }
        if (!STATUS_COMPLETED.equals(ride.getStatus())) {
            throw new IllegalStateException("Payment is allowed only after ride completion.");
        }
        if (ride.isPaid()) {
            throw new IllegalStateException("Payment has already been completed for this booking.");
        }
        if (InputValidator.isEmptyOrBlank(paymentMethod)) {
            throw new IllegalArgumentException("Payment method cannot be empty.");
        }

        if (ride.getFinalFare() == null) {
            ride.setFinalFare(ride.getEstimatedFare());
        }
        ride.setPaymentMethod(paymentMethod.trim().toUpperCase());
        ride.setPaid(true);
        return true;
    }

    public RideRecord getBookingById(int bookingId) {
        return bookings.get(bookingId);
    }

    public List<RideRecord> getBookingsByPassenger(int passengerId) {
        List<RideRecord> result = new ArrayList<>();
        for (RideRecord ride : bookings.values()) {
            if (ride.getPassengerId() == passengerId) {
                result.add(ride);
            }
        }
        return result;
    }

    public List<RideRecord> getBookingsForDriver(int driverId) {
        List<RideRecord> result = new ArrayList<>();
        for (RideRecord ride : bookings.values()) {
            if (ride.getDriverId() != null && ride.getDriverId() == driverId) {
                result.add(ride);
            }
        }
        return result;
    }

    public Integer getActiveBookingIdForDriver(int driverId) {
        return activeBookingByDriver.get(driverId);
    }

    private RideRecord requireBooking(int bookingId) {
        RideRecord ride = bookings.get(bookingId);
        if (ride == null) {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
        return ride;
    }
}