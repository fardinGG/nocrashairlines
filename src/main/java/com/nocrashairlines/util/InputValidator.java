package com.nocrashairlines.util;

import java.util.regex.Pattern;

/**
 * Input validation utility for various data types.
 * Supports data validation requirements across all FRs
 */
public class InputValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$" // E.164 format
    );
    
    private static final Pattern PASSPORT_PATTERN = Pattern.compile(
        "^[A-Z0-9]{6,9}$"
    );
    
    /**
     * Validate email address
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return new ValidationResult(false, "Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ValidationResult(false, "Invalid email format");
        }
        
        return new ValidationResult(true, "Email is valid");
    }
    
    /**
     * Validate phone number
     */
    public static ValidationResult validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return new ValidationResult(false, "Phone number cannot be empty");
        }
        
        // Remove spaces and dashes for validation
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return new ValidationResult(false, "Invalid phone number format");
        }
        
        return new ValidationResult(true, "Phone number is valid");
    }
    
    /**
     * Validate passport number
     */
    public static ValidationResult validatePassportNumber(String passport) {
        if (passport == null || passport.isEmpty()) {
            return new ValidationResult(false, "Passport number cannot be empty");
        }
        
        if (!PASSPORT_PATTERN.matcher(passport.toUpperCase()).matches()) {
            return new ValidationResult(false, 
                "Invalid passport number format (6-9 alphanumeric characters)");
        }
        
        return new ValidationResult(true, "Passport number is valid");
    }
    
    /**
     * Validate name (non-empty, alphabetic with spaces)
     */
    public static ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Name cannot be empty");
        }
        
        if (name.trim().length() < 2) {
            return new ValidationResult(false, "Name must be at least 2 characters");
        }
        
        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            return new ValidationResult(false, "Name contains invalid characters");
        }
        
        return new ValidationResult(true, "Name is valid");
    }
    
    /**
     * Validate amount (positive number)
     */
    public static ValidationResult validateAmount(double amount) {
        if (amount <= 0) {
            return new ValidationResult(false, "Amount must be greater than zero");
        }
        
        if (amount > 1000000) {
            return new ValidationResult(false, "Amount exceeds maximum limit");
        }
        
        return new ValidationResult(true, "Amount is valid");
    }
    
    /**
     * Validate seat number format (e.g., 12A, 5B)
     */
    public static ValidationResult validateSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return new ValidationResult(false, "Seat number cannot be empty");
        }
        
        if (!seatNumber.matches("^\\d{1,2}[A-F]$")) {
            return new ValidationResult(false, 
                "Invalid seat number format (e.g., 12A, 5B)");
        }
        
        return new ValidationResult(true, "Seat number is valid");
    }
}

