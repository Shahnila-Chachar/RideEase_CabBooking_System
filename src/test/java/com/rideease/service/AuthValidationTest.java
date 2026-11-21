package com.rideease.service;

import com.rideease.model.UserRole;
import com.rideease.validation.InputValidator;

public class AuthValidationTest {

    public static void main(String[] args) {
        testInputValidator();
        testPasswordHashing();

        System.out.println("ALL AUTHENTICATION TESTS PASSED!");
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

    private static void testPasswordHashing() {
        UserService userService = new UserService();

        String hash1 = userService.hashPassword("secret123");
        String hash2 = userService.hashPassword("secret123");

        check(!hash1.equals(hash2), "Salted hashes should be unique");

        check(userService.verifyPassword("secret123", hash1),
                "Correct password verification failed");

        check(!userService.verifyPassword("wrong", hash1),
                "Wrong password should not verify");

        check(!userService.verifyPassword("secret123", "invalid-hash"),
                "Invalid hash should fail verification");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Test failed: " + message);
        }
    }
}
