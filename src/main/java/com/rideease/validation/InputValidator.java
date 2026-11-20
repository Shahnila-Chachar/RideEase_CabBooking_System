package com.rideease.validation;

import com.rideease.model.UserRole;
import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MIN_PASSWORD_LENGTH = 6;

    public static boolean isEmptyOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidName(String name) {
        if (isEmptyOrBlank(name)) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= MIN_NAME_LENGTH && trimmed.matches("^[A-Za-z .'-]+$");
    }

    public static boolean isValidEmail(String email) {
        if (isEmptyOrBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (isEmptyOrBlank(phone)) {
            return false;
        }
        String cleaned = phone.trim().replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidRegistrationRole(UserRole role) {
        return role == UserRole.PASSENGER || role == UserRole.DRIVER;
    }
}