package com.nocrashairlines.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Test class for Booking model
 * Tests FR-4, FR-6, FR-7 (Ticket Booking, Booking Management, Reschedule)
 */
@DisplayName("Booking Model Tests")
class BookingTest {
    
    private Booking booking;
    
    @BeforeEach
    void setUp() {
        booking = new Booking(
            "BK-001",
            "PASS-001",
            "FL-001",
            "John Doe",
            "ECONOMY",
            299.99
        );
        booking.setPassengerEmail("john.doe@example.com");
        booking.setPassengerPhone("+1234567890");
        booking.setPassportNumber("AB123456");
        booking.setSeatNumber("12A");
    }
    
    @Test
    @DisplayName("Should create booking with valid data")
    void testBookingCreation() {
        assertNotNull(booking, "Booking should not be null");
        assertEquals("BK-001", booking.getBookingId(), "Booking ID should match");
        assertEquals("PASS-001", booking.getPassengerId(), "Passenger ID should match");
        assertEquals("FL-001", booking.getFlightId(), "Flight ID should match");
        assertEquals("John Doe", booking.getPassengerName(), "Passenger name should match");
        assertEquals("ECONOMY", booking.getTravelClass(), "Travel class should match");
        assertEquals(299.99, booking.getTotalAmount(), 0.01, "Total amount should match");
        assertEquals("PENDING", booking.getStatus(), "Initial status should be PENDING");
        assertFalse(booking.isCheckedIn(), "Should not be checked in initially");
    }
    
    @Test
    @DisplayName("Should confirm booking successfully")
    void testConfirmBooking() {
        assertEquals("PENDING", booking.getStatus(), "Initial status should be PENDING");
        
        LocalDateTime beforeConfirm = LocalDateTime.now();
        booking.confirmBooking();
        LocalDateTime afterConfirm = LocalDateTime.now();
        
        assertEquals("CONFIRMED", booking.getStatus(), "Status should be CONFIRMED");
        assertNotNull(booking.getLastModified(), "Last modified should be set");
        assertTrue(booking.getLastModified().isAfter(beforeConfirm.minusSeconds(1)), 
                   "Last modified should be recent");
        assertTrue(booking.getLastModified().isBefore(afterConfirm.plusSeconds(1)), 
                   "Last modified should be recent");
    }
    
    @Test
    @DisplayName("Should cancel booking successfully")
    void testCancelBooking() {
        booking.confirmBooking();
        assertTrue(booking.canBeCancelled(), "Confirmed booking should be cancellable");
        
        booking.cancelBooking();
        
        assertEquals("CANCELLED", booking.getStatus(), "Status should be CANCELLED");
        assertNotNull(booking.getLastModified(), "Last modified should be updated");
    }
    
    @Test
    @DisplayName("Should check if booking can be cancelled")
    void testCanBeCancelled() {
        booking.setStatus("PENDING");
        assertTrue(booking.canBeCancelled(), "PENDING booking should be cancellable");
        
        booking.setStatus("CONFIRMED");
        assertTrue(booking.canBeCancelled(), "CONFIRMED booking should be cancellable");
        
        booking.setStatus("CANCELLED");
        assertFalse(booking.canBeCancelled(), "CANCELLED booking should not be cancellable");
        
        booking.setStatus("RESCHEDULED");
        assertFalse(booking.canBeCancelled(), "RESCHEDULED booking should not be cancellable");
    }
    
    @Test
    @DisplayName("Should reschedule booking successfully")
    void testRescheduleBooking() {
        booking.confirmBooking();
        assertTrue(booking.canBeRescheduled(), "Confirmed booking should be reschedulable");
        
        String newFlightId = "FL-002";
        booking.rescheduleBooking(newFlightId);
        
        assertEquals("FL-002", booking.getFlightId(), "Flight ID should be updated");
        assertEquals("RESCHEDULED", booking.getStatus(), "Status should be RESCHEDULED");
        assertNotNull(booking.getLastModified(), "Last modified should be updated");
    }
    
    @Test
    @DisplayName("Should check if booking can be rescheduled")
    void testCanBeRescheduled() {
        booking.setStatus("PENDING");
        assertFalse(booking.canBeRescheduled(), "PENDING booking should not be reschedulable");
        
        booking.setStatus("CONFIRMED");
        assertTrue(booking.canBeRescheduled(), "CONFIRMED booking should be reschedulable");
        
        booking.setStatus("CANCELLED");
        assertFalse(booking.canBeRescheduled(), "CANCELLED booking should not be reschedulable");
    }
    
    @Test
    @DisplayName("Should set and get payment ID")
    void testPaymentId() {
        assertNull(booking.getPaymentId(), "Payment ID should be null initially");
        
        booking.setPaymentId("PAY-001");
        assertEquals("PAY-001", booking.getPaymentId(), "Payment ID should match");
    }
    
    @Test
    @DisplayName("Should handle check-in process")
    void testCheckIn() {
        assertFalse(booking.isCheckedIn(), "Should not be checked in initially");
        
        booking.setCheckedIn(true);
        booking.setBaggageTag("BAG-12345");
        
        assertTrue(booking.isCheckedIn(), "Should be checked in");
        assertEquals("BAG-12345", booking.getBaggageTag(), "Baggage tag should match");
    }
    
    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String result = booking.toString();
        
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("BK-001"), "Should contain booking ID");
        assertTrue(result.contains("John Doe"), "Should contain passenger name");
        assertTrue(result.contains("FL-001"), "Should contain flight ID");
        assertTrue(result.contains("12A"), "Should contain seat number");
        assertTrue(result.contains("ECONOMY"), "Should contain travel class");
        assertTrue(result.contains("PENDING"), "Should contain status");
    }
}

