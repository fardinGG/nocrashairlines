package com.nocrashairlines.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test class for Passenger model
 * Tests FR-1, FR-2, FR-10 (Passenger Registration, Login, Profile Management)
 */
@DisplayName("Passenger Model Tests")
class PassengerTest {
    
    private Passenger passenger;
    
    @BeforeEach
    void setUp() {
        passenger = new Passenger(
            "PASS-001",
            "John Doe",
            "john.doe@example.com",
            "hashedPassword123",
            "+1234567890",
            "AB123456"
        );
    }
    
    @Test
    @DisplayName("Should create passenger with valid data")
    void testPassengerCreation() {
        assertNotNull(passenger, "Passenger should not be null");
        assertEquals("PASS-001", passenger.getUserId(), "User ID should match");
        assertEquals("John Doe", passenger.getName(), "Name should match");
        assertEquals("john.doe@example.com", passenger.getEmail(), "Email should match");
        assertEquals("+1234567890", passenger.getPhoneNumber(), "Phone should match");
        assertEquals("AB123456", passenger.getPassportNumber(), "Passport should match");
        assertNotNull(passenger.getBookingIds(), "Booking IDs list should be initialized");
        assertTrue(passenger.getBookingIds().isEmpty(), "Booking IDs should be empty initially");
    }
    
    @Test
    @DisplayName("Should add booking to passenger")
    void testAddBooking() {
        String bookingId1 = "BK-001";
        String bookingId2 = "BK-002";
        
        passenger.addBooking(bookingId1);
        passenger.addBooking(bookingId2);
        
        List<String> bookings = passenger.getBookingIds();
        assertEquals(2, bookings.size(), "Should have 2 bookings");
        assertTrue(bookings.contains(bookingId1), "Should contain first booking");
        assertTrue(bookings.contains(bookingId2), "Should contain second booking");
    }
    
    @Test
    @DisplayName("Should remove booking from passenger")
    void testRemoveBooking() {
        String bookingId1 = "BK-001";
        String bookingId2 = "BK-002";
        
        passenger.addBooking(bookingId1);
        passenger.addBooking(bookingId2);
        
        passenger.removeBooking(bookingId1);
        
        List<String> bookings = passenger.getBookingIds();
        assertEquals(1, bookings.size(), "Should have 1 booking after removal");
        assertFalse(bookings.contains(bookingId1), "Should not contain removed booking");
        assertTrue(bookings.contains(bookingId2), "Should still contain second booking");
    }
    
    @Test
    @DisplayName("Should set and get preferred class")
    void testPreferredClass() {
        assertNull(passenger.getPreferredClass(), "Preferred class should be null initially");
        
        passenger.setPreferredClass("BUSINESS");
        assertEquals("BUSINESS", passenger.getPreferredClass(), "Preferred class should be BUSINESS");
        
        passenger.setPreferredClass("FIRST_CLASS");
        assertEquals("FIRST_CLASS", passenger.getPreferredClass(), "Preferred class should be FIRST_CLASS");
    }
    
    @Test
    @DisplayName("Should set and get address")
    void testAddress() {
        assertNull(passenger.getAddress(), "Address should be null initially");
        
        String address = "123 Main St, Toronto, ON M5V 3A8";
        passenger.setAddress(address);
        assertEquals(address, passenger.getAddress(), "Address should match");
    }
    
    @Test
    @DisplayName("Should update passenger profile information")
    void testUpdateProfile() {
        passenger.setName("Jane Doe");
        passenger.setEmail("jane.doe@example.com");
        passenger.setPhoneNumber("+9876543210");
        passenger.setAddress("456 Oak Ave, Vancouver, BC V6B 1A1");
        passenger.setPreferredClass("ECONOMY");
        
        assertEquals("Jane Doe", passenger.getName(), "Name should be updated");
        assertEquals("jane.doe@example.com", passenger.getEmail(), "Email should be updated");
        assertEquals("+9876543210", passenger.getPhoneNumber(), "Phone should be updated");
        assertEquals("456 Oak Ave, Vancouver, BC V6B 1A1", passenger.getAddress(), "Address should be updated");
        assertEquals("ECONOMY", passenger.getPreferredClass(), "Preferred class should be updated");
    }
    
    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String result = passenger.toString();
        
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("PASS-001"), "Should contain user ID");
        assertTrue(result.contains("John Doe"), "Should contain name");
        assertTrue(result.contains("john.doe@example.com"), "Should contain email");
        assertTrue(result.contains("AB123456"), "Should contain passport number");
    }
    
    @Test
    @DisplayName("Should handle multiple bookings correctly")
    void testMultipleBookings() {
        for (int i = 1; i <= 5; i++) {
            passenger.addBooking("BK-00" + i);
        }
        
        assertEquals(5, passenger.getBookingIds().size(), "Should have 5 bookings");
        
        passenger.removeBooking("BK-003");
        assertEquals(4, passenger.getBookingIds().size(), "Should have 4 bookings after removal");
    }
}

