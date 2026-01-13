package com.nocrashairlines.util;

import java.util.regex.Pattern;

/**
 * Password validation utility to enforce password policies.
 * Supports NFR-6 (Password Policy / Security)
 * Requirements: min 8 characters, 1 number, 1 special character
 */
public class PasswordValidator {
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    
    /**
     * Validate password against security policy
     * @param password Password to validate
     * @return ValidationResult containing validation status and message
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password cannot be empty");
        }
        
        if (password.length() < MIN_LENGTH) {
            return new ValidationResult(false, 
                "Password must be at least " + MIN_LENGTH + " characters long");
        }
        
        if (!NUMBER_PATTERN.matcher(password).matches()) {
            return new ValidationResult(false, 
                "Password must contain at least one number");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            return new ValidationResult(false, 
                "Password must contain at least one special character");
        }
        
        return new ValidationResult(true, "Password is valid");
    }
    
    /**
     * Check if password is strong (additional checks beyond minimum requirements)
     * @param password Password to check
     * @return true if password is strong, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 12) {
            return false;
        }
        
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasNumber = NUMBER_PATTERN.matcher(password).matches();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).matches();
        
        return hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar;
    }
}

