package com.nocrashairlines.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PasswordValidator utility
 * Tests NFR-6 (Password Policy and Security)
 */
@DisplayName("Password Validator Tests")
class PasswordValidatorTest {
    
    @Test
    @DisplayName("Should accept valid password with all requirements")
    void testValidPassword() {
        ValidationResult result = PasswordValidator.validatePassword("SecurePass123!");
        
        assertTrue(result.isValid(), "Password should be valid");
        assertEquals("Password is valid", result.getMessage(), "Message should indicate validity");
    }
    
    @Test
    @DisplayName("Should reject null password")
    void testNullPassword() {
        ValidationResult result = PasswordValidator.validatePassword(null);
        
        assertFalse(result.isValid(), "Null password should be invalid");
        assertEquals("Password cannot be empty", result.getMessage(), "Should have empty message");
    }
    
    @Test
    @DisplayName("Should reject empty password")
    void testEmptyPassword() {
        ValidationResult result = PasswordValidator.validatePassword("");
        
        assertFalse(result.isValid(), "Empty password should be invalid");
        assertEquals("Password cannot be empty", result.getMessage(), "Should have empty message");
    }
    
    @Test
    @DisplayName("Should reject password shorter than 8 characters")
    void testShortPassword() {
        ValidationResult result = PasswordValidator.validatePassword("Pass1!");
        
        assertFalse(result.isValid(), "Short password should be invalid");
        assertTrue(result.getMessage().contains("at least 8 characters"), 
                   "Should mention minimum length");
    }
    
    @Test
    @DisplayName("Should reject password without numbers")
    void testPasswordWithoutNumbers() {
        ValidationResult result = PasswordValidator.validatePassword("SecurePass!");
        
        assertFalse(result.isValid(), "Password without numbers should be invalid");
        assertTrue(result.getMessage().contains("at least one number"), 
                   "Should mention number requirement");
    }
    
    @Test
    @DisplayName("Should reject password without special characters")
    void testPasswordWithoutSpecialChars() {
        ValidationResult result = PasswordValidator.validatePassword("SecurePass123");
        
        assertFalse(result.isValid(), "Password without special chars should be invalid");
        assertTrue(result.getMessage().contains("special character"), 
                   "Should mention special character requirement");
    }
    
    @Test
    @DisplayName("Should accept password with various special characters")
    void testPasswordWithDifferentSpecialChars() {
        String[] validPasswords = {
            "Password1!",
            "Password2@",
            "Password3#",
            "Password4$",
            "Password5%",
            "Password6^",
            "Password7&",
            "Password8*"
        };
        
        for (String password : validPasswords) {
            ValidationResult result = PasswordValidator.validatePassword(password);
            assertTrue(result.isValid(), "Password '" + password + "' should be valid");
        }
    }
    
    @Test
    @DisplayName("Should identify strong passwords")
    void testStrongPassword() {
        assertTrue(PasswordValidator.isStrongPassword("VerySecurePass123!"), 
                   "Should identify strong password");
        assertTrue(PasswordValidator.isStrongPassword("MyP@ssw0rd2024"), 
                   "Should identify strong password");
    }
    
    @Test
    @DisplayName("Should reject weak passwords as not strong")
    void testWeakPassword() {
        assertFalse(PasswordValidator.isStrongPassword("Pass123!"), 
                    "Short password should not be strong");
        assertFalse(PasswordValidator.isStrongPassword("password123!"), 
                    "Password without uppercase should not be strong");
        assertFalse(PasswordValidator.isStrongPassword("PASSWORD123!"), 
                    "Password without lowercase should not be strong");
        assertFalse(PasswordValidator.isStrongPassword("PasswordABC!"), 
                    "Password without numbers should not be strong");
        assertFalse(PasswordValidator.isStrongPassword("Password1234"), 
                    "Password without special chars should not be strong");
        assertFalse(PasswordValidator.isStrongPassword(null), 
                    "Null password should not be strong");
    }
    
    @Test
    @DisplayName("Should validate minimum length requirement")
    void testMinimumLength() {
        ValidationResult result1 = PasswordValidator.validatePassword("Pass12!");
        assertFalse(result1.isValid(), "7 characters should be invalid");
        
        ValidationResult result2 = PasswordValidator.validatePassword("Pass123!");
        assertTrue(result2.isValid(), "8 characters should be valid");
    }
    
    @Test
    @DisplayName("Should handle edge cases")
    void testEdgeCases() {
        // Password with only spaces
        ValidationResult result1 = PasswordValidator.validatePassword("        ");
        assertFalse(result1.isValid(), "Password with only spaces should be invalid");
        
        // Very long password
        String longPassword = "VeryLongPassword123!".repeat(5);
        ValidationResult result2 = PasswordValidator.validatePassword(longPassword);
        assertTrue(result2.isValid(), "Very long password should be valid if it meets requirements");
    }
}

