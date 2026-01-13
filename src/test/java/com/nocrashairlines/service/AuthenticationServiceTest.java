package com.nocrashairlines.service;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.model.Admin;
import com.nocrashairlines.model.Passenger;
import com.nocrashairlines.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    
    private AuthenticationService authService;
    
    @BeforeEach
    void setUp() {
        authService = new AuthenticationService();
    }
    
    @Test
    @DisplayName("Should login admin with default credentials")
    void testAdminLogin() throws AuthenticationException {
        Admin admin = authService.loginAdmin("admin@nocrashairlines.com", "Admin@123");
        
        assertNotNull(admin);
        assertEquals("admin@nocrashairlines.com", admin.getEmail());
    }
    
    @Test
    @DisplayName("Should register passenger with valid data")
    void testPassengerRegistration() throws AuthenticationException {
        Passenger passenger = authService.registerPassenger(
            "Test User", 
            "test@example.com", 
            "TestPass@123", 
            "+1234567890", 
            "AB123456"
        );
        
        assertNotNull(passenger);
        assertEquals("Test User", passenger.getName());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid admin login")
    void testInvalidAdminLogin() {
        assertThrows(AuthenticationException.class, () -> {
            authService.loginAdmin("wrong@email.com", "wrongpass");
        });
    }
}
