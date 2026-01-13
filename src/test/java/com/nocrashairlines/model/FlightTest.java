package com.nocrashairlines.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Test class for Flight model
 * Tests FR-3, FR-12, FR-13 (Flight Search, Management, Seat Inventory)
 */
@DisplayName("Flight Model Tests")
class FlightTest {
    
    private Flight flight;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    
    @BeforeEach
    void setUp() {
        departureTime = LocalDateTime.now().plusDays(1);
        arrivalTime = departureTime.plusHours(3);
        
        flight = new Flight(
            "FL-001",
            "NC101",
            "Toronto",
            "Vancouver",
            departureTime,
            arrivalTime,
            180
        );
        
        flight.setAircraftType("Boeing 737");
        flight.setGate("A12");
        flight.setClassPrice("ECONOMY", 299.99);
        flight.setClassPrice("BUSINESS", 799.99);
        flight.setClassPrice("FIRST_CLASS", 1499.99);
    }
    
    @Test
    @DisplayName("Should create flight with valid data")
    void testFlightCreation() {
        assertNotNull(flight, "Flight should not be null");
        assertEquals("FL-001", flight.getFlightId(), "Flight ID should match");
        assertEquals("NC101", flight.getFlightNumber(), "Flight number should match");
        assertEquals("Toronto", flight.getOrigin(), "Origin should match");
        assertEquals("Vancouver", flight.getDestination(), "Destination should match");
        assertEquals(180, flight.getTotalSeats(), "Total seats should match");
        assertEquals(180, flight.getAvailableSeats(), "Available seats should equal total initially");
        assertEquals("SCHEDULED", flight.getStatus(), "Initial status should be SCHEDULED");
    }
    
    @Test
    @DisplayName("Should reserve seat successfully")
    void testReserveSeat() {
        assertTrue(flight.hasAvailableSeats(), "Should have available seats");
        
        boolean reserved = flight.reserveSeat();
        
        assertTrue(reserved, "Seat reservation should succeed");
        assertEquals(179, flight.getAvailableSeats(), "Available seats should decrease by 1");
    }
    
    @Test
    @DisplayName("Should not reserve seat when flight is full")
    void testReserveSeatWhenFull() {
        // Reserve all seats
        for (int i = 0; i < 180; i++) {
            flight.reserveSeat();
        }
        
        assertFalse(flight.hasAvailableSeats(), "Should have no available seats");
        
        boolean reserved = flight.reserveSeat();
        
        assertFalse(reserved, "Seat reservation should fail when full");
        assertEquals(0, flight.getAvailableSeats(), "Available seats should remain 0");
    }
    
    @Test
    @DisplayName("Should release seat successfully")
    void testReleaseSeat() {
        flight.reserveSeat();
        flight.reserveSeat();
        assertEquals(178, flight.getAvailableSeats(), "Should have 178 available seats");
        
        flight.releaseSeat();
        
        assertEquals(179, flight.getAvailableSeats(), "Available seats should increase by 1");
    }
    
    @Test
    @DisplayName("Should not release seat beyond total capacity")
    void testReleaseSeatBeyondCapacity() {
        assertEquals(180, flight.getAvailableSeats(), "Should start with 180 seats");
        
        flight.releaseSeat();
        
        assertEquals(180, flight.getAvailableSeats(), "Should not exceed total seats");
    }
    
    @Test
    @DisplayName("Should set and get class prices")
    void testClassPrices() {
        assertEquals(299.99, flight.getClassPrice("ECONOMY"), 0.01, "Economy price should match");
        assertEquals(799.99, flight.getClassPrice("BUSINESS"), 0.01, "Business price should match");
        assertEquals(1499.99, flight.getClassPrice("FIRST_CLASS"), 0.01, "First class price should match");
        
        flight.setClassPrice("ECONOMY", 349.99);
        assertEquals(349.99, flight.getClassPrice("ECONOMY"), 0.01, "Updated economy price should match");
    }
    
    @Test
    @DisplayName("Should update flight status")
    void testFlightStatus() {
        assertEquals("SCHEDULED", flight.getStatus(), "Initial status should be SCHEDULED");
        
        flight.setStatus("DELAYED");
        assertEquals("DELAYED", flight.getStatus(), "Status should be DELAYED");
        
        flight.setStatus("DEPARTED");
        assertEquals("DEPARTED", flight.getStatus(), "Status should be DEPARTED");
        
        flight.setStatus("ARRIVED");
        assertEquals("ARRIVED", flight.getStatus(), "Status should be ARRIVED");
    }
    
    @Test
    @DisplayName("Should test flight equality")
    void testFlightEquality() {
        Flight sameFlight = new Flight(
            "FL-001",
            "NC101",
            "Toronto",
            "Vancouver",
            departureTime,
            arrivalTime,
            180
        );
        
        assertEquals(flight, sameFlight, "Flights with same ID and number should be equal");
        assertEquals(flight.hashCode(), sameFlight.hashCode(), "Hash codes should match");
    }
    
    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String result = flight.toString();
        
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("NC101"), "Should contain flight number");
        assertTrue(result.contains("Toronto"), "Should contain origin");
        assertTrue(result.contains("Vancouver"), "Should contain destination");
        assertTrue(result.contains("SCHEDULED"), "Should contain status");
        assertTrue(result.contains("A12"), "Should contain gate");
    }
}

