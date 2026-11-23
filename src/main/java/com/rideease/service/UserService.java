package com.rideease.service;

import com.rideease.dao.RegistrationDAO;
import com.rideease.dao.UserDAO;
import com.rideease.model.Driver;
import com.rideease.model.User;
import com.rideease.model.UserRole;
import com.rideease.model.Vehicle;
import com.rideease.validation.InputValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;

public class UserService {

    private final UserDAO userDAO;
    private final RegistrationDAO registrationDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.registrationDAO = new RegistrationDAO();
    }

    public User registerPassenger(String fullName, String email, String phone,
                                  String password) {

        User user = createUser(fullName, email, phone, password, UserRole.PASSENGER);

        try {
            registrationDAO.registerPassenger(user);
            return user;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Passenger registration failed: " + e.getMessage(), e);
        }
    }

    public User registerDriver(String fullName, String email, String phone,
                               String password, String licenseNumber,
                               String city, String serviceArea,
                               String vehicleType, String vehicleNumber,
                               String model, String color) {

        User user = createUser(fullName, email, phone, password, UserRole.DRIVER);

        requireText(licenseNumber, "License number");
        requireText(city, "City");
        requireText(serviceArea, "Service area");
        requireText(vehicleNumber, "Vehicle number");
        requireText(model, "Vehicle model");
        requireText(color, "Vehicle color");

        String normalizedType = vehicleType == null
                ? "" : vehicleType.trim().toUpperCase(Locale.ROOT);

        if (!normalizedType.equals("RICKSHAW")
                && !normalizedType.equals("CAB")) {
            throw new IllegalArgumentException(
                    "Vehicle type must be RICKSHAW or CAB.");
        }

        Driver driver = new Driver(
                0, user.getUserId(), licenseNumber.trim(),
                city.trim(), serviceArea.trim(), false, null
        );

        Vehicle vehicle = new Vehicle(
                0, 0, normalizedType, vehicleNumber.trim(),
                model.trim(), color.trim(), null
        );

        try {
            registrationDAO.registerDriver(user, driver, vehicle);
            return user;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Driver registration failed. Check duplicate license or vehicle number. "
                            + e.getMessage(), e);
        }
    }

    private User createUser(String fullName, String email, String phone,
                            String plainPassword, UserRole role) {

        if (!InputValidator.isValidName(fullName)) {
            throw new IllegalArgumentException("Invalid full name.");
        }

        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (!InputValidator.isValidPhoneNumber(phone)) {
            throw new IllegalArgumentException("Invalid phone number.");
        }

        if (!InputValidator.isValidPassword(plainPassword)) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters long.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedPhone = phone.trim().replaceAll("[\\s-]", "");

        if (userDAO.emailExists(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (userDAO.phoneExists(normalizedPhone)) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }

        return new User(
                0, fullName.trim(), normalizedEmail, normalizedPhone,
                hashPassword(plainPassword), role
        );
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
    }

    public User register(String fullName, String email, String phone,
                         String plainPassword, UserRole role) {

        if (role == UserRole.PASSENGER) {
            return registerPassenger(fullName, email, phone, plainPassword);
        }

        throw new IllegalArgumentException(
                "Use driver registration with driver and vehicle details.");
    }

    public User login(String email, String plainPassword) {

        if (!InputValidator.isValidEmail(email)
                || InputValidator.isEmptyOrBlank(plainPassword)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        User user = userDAO.findByEmail(normalizedEmail);

        if (user == null || !verifyPassword(plainPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }

    public boolean isEmailRegistered(String email) {
        if (InputValidator.isEmptyOrBlank(email)) {
            return false;
        }

        return userDAO.emailExists(email.trim().toLowerCase(Locale.ROOT));
    }

    public User findByEmail(String email) {
        if (InputValidator.isEmptyOrBlank(email)) {
            return null;
        }

        return userDAO.findByEmail(email.trim().toLowerCase(Locale.ROOT));
    }

    public String hashPassword(String plainPassword) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = sha256WithSalt(plainPassword, salt);

        return saltBase64 + ":" + hashBase64;
    }

    public boolean verifyPassword(String plainPassword, String storedPasswordHash) {

        if (plainPassword == null || storedPasswordHash == null
                || !storedPasswordHash.contains(":")) {
            return false;
        }

        try {
            String[] parts = storedPasswordHash.split(":", 2);
            byte[] salt = Base64.getDecoder().decode(parts[0]);

            String expectedHash = parts[1];
            String actualHash = sha256WithSalt(plainPassword, salt);

            return MessageDigest.isEqual(
                    expectedHash.getBytes(StandardCharsets.UTF_8),
                    actualHash.getBytes(StandardCharsets.UTF_8)
            );

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String sha256WithSalt(String plainPassword, byte[] salt) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);

            byte[] hashedBytes = digest.digest(
                    plainPassword.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available.", e);
        }
    }
}
