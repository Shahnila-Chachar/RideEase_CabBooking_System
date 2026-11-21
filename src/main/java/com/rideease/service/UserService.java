package com.rideease.service;

import com.rideease.dao.UserDAO;
import com.rideease.model.User;
import com.rideease.model.UserRole;
import com.rideease.validation.InputValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User register(String fullName, String email, String phone,
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
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (!InputValidator.isValidRegistrationRole(role)) {
            throw new IllegalArgumentException("Role must be PASSENGER or DRIVER.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedPhone = phone.trim();

        if (userDAO.emailExists(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (userDAO.phoneExists(normalizedPhone)) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }

        String hashedPassword = hashPassword(plainPassword);

        User newUser = new User(
                0,
                fullName.trim(),
                normalizedEmail,
                normalizedPhone,
                hashedPassword,
                role
        );

        if (!userDAO.registerUser(newUser)) {
            throw new IllegalStateException("User registration failed.");
        }

        return newUser;
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
                    plainPassword.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available.", e);
        }
    }
}
