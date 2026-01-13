package com.nocrashairlines.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InputValidator utility
 * Tests data validation requirements across all functional requirements
 */
@DisplayName("Input Validator Tests")
class InputValidatorTest {
    
    // Email Validation Tests
    
    @Test
    @DisplayName("Should accept valid email addresses")
    void testValidEmails() {
        String[] validEmails = {
            "user@example.com",
            "john.doe@nocrashairlines.com",
            "admin123@test.co.uk",
            "test+tag@domain.org"
        };
        
        for (String email : validEmails) {
            ValidationResult result = InputValidator.validateEmail(email);
            assertTrue(result.isValid(), "Email '" + email + "' should be valid");
        }
    }
    
    @Test
    @DisplayName("Should reject invalid email addresses")
    void testInvalidEmails() {
        String[] invalidEmails = {
            "notanemail",
            "@example.com",
            "user@",
            "user @example.com",
            "user@.com"
        };
        
        for (String email : invalidEmails) {
            ValidationResult result = InputValidator.validateEmail(email);
            assertFalse(result.isValid(), "Email '" + email + "' should be invalid");
        }
    }
    
    @Test
    @DisplayName("Should reject null or empty email")
    void testNullOrEmptyEmail() {
        ValidationResult result1 = InputValidator.validateEmail(null);
        assertFalse(result1.isValid(), "Null email should be invalid");
        
        ValidationResult result2 = InputValidator.validateEmail("");
        assertFalse(result2.isValid(), "Empty email should be invalid");
    }
    
    // Phone Number Validation Tests
    
    @Test
    @DisplayName("Should accept valid phone numbers")
    void testValidPhoneNumbers() {
        String[] validPhones = {
            "+1234567890",
            "+14165551234",
            "+442071234567",
            "1234567890"
        };
        
        for (String phone : validPhones) {
            ValidationResult result = InputValidator.validatePhoneNumber(phone);
            assertTrue(result.isValid(), "Phone '" + phone + "' should be valid");
        }
    }
    
    @Test
    @DisplayName("Should accept phone numbers with formatting")
    void testFormattedPhoneNumbers() {
        ValidationResult result1 = InputValidator.validatePhoneNumber("+1 416 555 1234");
        assertTrue(result1.isValid(), "Phone with spaces should be valid");
        
        ValidationResult result2 = InputValidator.validatePhoneNumber("+1-416-555-1234");
        assertTrue(result2.isValid(), "Phone with dashes should be valid");
    }
    
    @Test
    @DisplayName("Should reject invalid phone numbers")
    void testInvalidPhoneNumbers() {
        ValidationResult result1 = InputValidator.validatePhoneNumber("1");
        assertFalse(result1.isValid(), "Single digit phone should be invalid");

        ValidationResult result2 = InputValidator.validatePhoneNumber("abcdefghij");
        assertFalse(result2.isValid(), "Alphabetic phone should be invalid");

        ValidationResult result3 = InputValidator.validatePhoneNumber("+0123456789");
        assertFalse(result3.isValid(), "Phone starting with 0 should be invalid");

        ValidationResult result4 = InputValidator.validatePhoneNumber("123456789012345678");
        assertFalse(result4.isValid(), "Too long phone should be invalid");
    }
    
    // Passport Number Validation Tests
    
    @Test
    @DisplayName("Should accept valid passport numbers")
    void testValidPassportNumbers() {
        String[] validPassports = {
            "AB123456",
            "XY987654",
            "A1B2C3D4",
            "123456789"
        };
        
        for (String passport : validPassports) {
            ValidationResult result = InputValidator.validatePassportNumber(passport);
            assertTrue(result.isValid(), "Passport '" + passport + "' should be valid");
        }
    }
    
    @Test
    @DisplayName("Should reject invalid passport numbers")
    void testInvalidPassportNumbers() {
        ValidationResult result1 = InputValidator.validatePassportNumber("12345");
        assertFalse(result1.isValid(), "Too short passport should be invalid");
        
        ValidationResult result2 = InputValidator.validatePassportNumber("1234567890");
        assertFalse(result2.isValid(), "Too long passport should be invalid");
        
        ValidationResult result3 = InputValidator.validatePassportNumber("AB-12345");
        assertFalse(result3.isValid(), "Passport with special chars should be invalid");
    }
    
    // Name Validation Tests
    
    @Test
    @DisplayName("Should accept valid names")
    void testValidNames() {
        String[] validNames = {
            "John Doe",
            "Mary-Jane Smith",
            "O'Connor",
            "Jean-Pierre"
        };
        
        for (String name : validNames) {
            ValidationResult result = InputValidator.validateName(name);
            assertTrue(result.isValid(), "Name '" + name + "' should be valid");
        }
    }
    
    @Test
    @DisplayName("Should reject invalid names")
    void testInvalidNames() {
        ValidationResult result1 = InputValidator.validateName("A");
        assertFalse(result1.isValid(), "Single character name should be invalid");
        
        ValidationResult result2 = InputValidator.validateName("John123");
        assertFalse(result2.isValid(), "Name with numbers should be invalid");
        
        ValidationResult result3 = InputValidator.validateName("");
        assertFalse(result3.isValid(), "Empty name should be invalid");
    }
    
    // Amount Validation Tests
    
    @Test
    @DisplayName("Should accept valid amounts")
    void testValidAmounts() {
        assertTrue(InputValidator.validateAmount(0.01).isValid(), "Small amount should be valid");
        assertTrue(InputValidator.validateAmount(100.00).isValid(), "Normal amount should be valid");
        assertTrue(InputValidator.validateAmount(999999.99).isValid(), "Large amount should be valid");
    }
    
    @Test
    @DisplayName("Should reject invalid amounts")
    void testInvalidAmounts() {
        assertFalse(InputValidator.validateAmount(0).isValid(), "Zero amount should be invalid");
        assertFalse(InputValidator.validateAmount(-10.00).isValid(), "Negative amount should be invalid");
        assertFalse(InputValidator.validateAmount(1000001).isValid(), "Excessive amount should be invalid");
    }
    
    // Seat Number Validation Tests
    
    @Test
    @DisplayName("Should accept valid seat numbers")
    void testValidSeatNumbers() {
        String[] validSeats = {"1A", "12B", "25C", "99F"};
        
        for (String seat : validSeats) {
            ValidationResult result = InputValidator.validateSeatNumber(seat);
            assertTrue(result.isValid(), "Seat '" + seat + "' should be valid");
        }
    }
    
    @Test
    @DisplayName("Should reject invalid seat numbers")
    void testInvalidSeatNumbers() {
        assertFalse(InputValidator.validateSeatNumber("1G").isValid(), "Seat with G should be invalid");
        assertFalse(InputValidator.validateSeatNumber("123A").isValid(), "3-digit seat should be invalid");
        assertFalse(InputValidator.validateSeatNumber("A1").isValid(), "Letter-first seat should be invalid");
    }
}

