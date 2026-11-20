package com.rideease.service;

import com.rideease.model.User;
import com.rideease.model.UserRole;
import com.rideease.validation.InputValidator;

public class AuthValidationTest {

    public static void main(String[] args) {
        testInputValidator();
        testUserService();
        System.out.println("ALL DAY 3 TESTS PASSED!");
    }

    private static void testInputValidator() {
        check(InputValidator.isEmptyOrBlank(null), "Null should be blank");
        check(InputValidator.isEmptyOrBlank("   "), "Spaces should be blank");
        check(!InputValidator.isEmptyOrBlank("Ayesha"), "Valid text should not be blank");

        check(InputValidator.isValidName("Ayesha Khan"), "Valid name failed");
        check(!InputValidator.isValidName("A"), "Single letter name should fail");
        check(!InputValidator.isValidName("Ayesha123"), "Name with numbers should fail");

        check(InputValidator.isValidEmail("ayesha@rideease.com"), "Valid email failed");
        check(!InputValidator.isValidEmail("invalid-email"), "Invalid email should fail");

        check(InputValidator.isValidPhoneNumber("03001234567"), "Local phone failed");
        check(InputValidator.isValidPhoneNumber("+923001234567"), "International phone failed");
        check(!InputValidator.isValidPhoneNumber("12345"), "Short phone should fail");

        check(InputValidator.isValidPassword("secret1"), "Valid password failed");
        check(!InputValidator.isValidPassword("12345"), "Short password should fail");

        check(InputValidator.isValidRegistrationRole(UserRole.PASSENGER), "PASSENGER role failed");
        check(InputValidator.isValidRegistrationRole(UserRole.DRIVER), "DRIVER role failed");
        check(!InputValidator.isValidRegistrationRole(UserRole.ADMIN), "ADMIN registration should fail");
    }

    private static void testUserService() {
        UserService userService = new UserService();

        User user = userService.register("Ayesha Khan", "Ayesha@RideEase.com", "pass123", UserRole.PASSENGER);
        check(user != null, "Registered user should not be null");
        check(user.getUserId() == 1, "First user ID should be 1");
        check("ayesha@rideease.com".equals(user.getEmail()), "Email should be normalized");
        check(!"pass123".equals(user.getPasswordHash()), "Password must be hashed");

        boolean duplicateBlocked = false;
        try {
            userService.register("Another User", "AYESHA@rideease.com", "pass456", UserRole.DRIVER);
        } catch (IllegalArgumentException e) {
            duplicateBlocked = true;
        }
        check(duplicateBlocked, "Duplicate email should throw exception");

        User loggedIn = userService.login("ayesha@rideease.com", "pass123");
        check(loggedIn != null && "Ayesha Khan".equals(loggedIn.getFullName()), "Valid login failed");

        boolean wrongPasswordBlocked = false;
        try {
            userService.login("ayesha@rideease.com", "wrongpass");
        } catch (IllegalArgumentException e) {
            wrongPasswordBlocked = true;
        }
        check(wrongPasswordBlocked, "Wrong password should throw exception");

        String hash1 = userService.hashPassword("secret123");
        String hash2 = userService.hashPassword("secret123");
        check(!hash1.equals(hash2), "Salted hashes should be unique");
        check(userService.verifyPassword("secret123", hash1), "Hash verification failed");
        check(!userService.verifyPassword("wrong", hash1), "Wrong password should not verify");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Test failed: " + message);
        }
    }
}