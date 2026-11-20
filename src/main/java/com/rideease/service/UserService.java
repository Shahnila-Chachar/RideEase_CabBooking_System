package com.rideease.service;

import com.rideease.model.User;
import com.rideease.model.UserRole;
import com.rideease.validation.InputValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final Map<String, User> usersByEmail = new HashMap<>();
    private int nextUserId = 1;

    public User register(String fullName, String email, String plainPassword, UserRole role) {
        if (!InputValidator.isValidName(fullName)) {
            throw new IllegalArgumentException("Invalid full name.");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!InputValidator.isValidPassword(plainPassword)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!InputValidator.isValidRegistrationRole(role)) {
            throw new IllegalArgumentException("Role must be PASSENGER or DRIVER.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (isEmailRegistered(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        String hashedPassword = hashPassword(plainPassword);
        User newUser = new User(nextUserId++, fullName.trim(), normalizedEmail, hashedPassword, role);
        usersByEmail.put(normalizedEmail, newUser);
        return newUser;
    }

    public User login(String email, String plainPassword) {
        if (!InputValidator.isValidEmail(email) || InputValidator.isEmptyOrBlank(plainPassword)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = usersByEmail.get(normalizedEmail);
        if (user == null || !verifyPassword(plainPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }

    public boolean isEmailRegistered(String email) {
        if (InputValidator.isEmptyOrBlank(email)) {
            return false;
        }
        return usersByEmail.containsKey(email.trim().toLowerCase());
    }

    public User findByEmail(String email) {
        if (InputValidator.isEmptyOrBlank(email)) {
            return null;
        }
        return usersByEmail.get(email.trim().toLowerCase());
    }

    public String hashPassword(String plainPassword) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = sha256WithSalt(plainPassword, salt);
        return saltBase64 + ":" + hashBase64;
    }

    public boolean verifyPassword(String plainPassword, String storedPasswordHash) {
        if (plainPassword == null || storedPasswordHash == null || !storedPasswordHash.contains(":")) {
            return false;
        }
        String[] parts = storedPasswordHash.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String expectedHash = parts[1];
        String actualHash = sha256WithSalt(plainPassword, salt);
        return MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.UTF_8),
                actualHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String sha256WithSalt(String plainPassword, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashedBytes = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available.", e);
        }
    }
}