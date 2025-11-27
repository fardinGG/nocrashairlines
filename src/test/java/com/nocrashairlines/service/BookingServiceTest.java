package com.nocrashairlines.service;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Flight;
import com.nocrashairlines.model.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Service Tests")
class BookingServiceTest {
    
    private BookingService bookingService;
    private AuthenticationService authService;
    private AdminService adminService;
    private Passenger testPassenger;
    private Flight testFlight;
    
    @BeforeEach
    void setUp() throws AuthenticationException {
        bookingService = new BookingService();
        authService = new AuthenticationService();
        adminService = new AdminService();
        System.out.println("Setting up BookingService test...");
        
        // Create test passenger
        testPassenger = authService.registerPassenger(
            "Booking User", 
            "booking@test.com", 
            "BookPass@123", 
            "+1234567890", 
            "BK123456"
        );
        
        // Create test flight
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        testFlight = adminService.addFlight(
            "BOOK101", 
            "Toronto", 
            "Montreal", 
            tomorrow, 
            tomorrow.plusHours(2), 
            "Boeing 737", 
            180, 
            "C3"
        );
        
        System.out.println("✓ Test data created: Passenger " + testPassenger.getUserId() + 
                          ", Flight " + testFlight.getFlightId());
    }
    
    @Test
    @DisplayName("Should create booking with valid details")
    void testCreateBooking() throws BookingException {
        System.out.println("Testing create booking...");
        
        Booking booking = bookingService.createBooking(
            testPassenger.getUserId(),
            testFlight.getFlightId(),
            testPassenger.getName(),
            testPassenger.getEmail(),
            testPassenger.getPhoneNumber(),
            testPassenger.getPassportNumber(),
            "ECONOMY"
        );
        
        assertNotNull(booking, "Booking should not be null");
        assertEquals(testPassenger.getUserId(), booking.getPassengerId(), "Passenger ID should match");
        assertEquals(testFlight.getFlightId(), booking.getFlightId(), "Flight ID should match");
        assertEquals("PENDING", booking.getStatus(), "Status should be PENDING");
        assertEquals("ECONOMY", booking.getTravelClass(), "Seat class should match");
        
        System.out.println("✓ Booking created: " + booking.getBookingId());
        System.out.println("✓ Create booking test passed!");
    }
    
    @Test
    @DisplayName("Should throw exception for invalid flight ID")
    void testCreateBookingInvalidFlight() {
        System.out.println("Testing create booking with invalid flight...");
        
        BookingException exception = assertThrows(BookingException.class, () -> {
            bookingService.createBooking(
                testPassenger.getUserId(),
                "INVALID_FLIGHT_ID",
                testPassenger.getName(),
                testPassenger.getEmail(),
                testPassenger.getPhoneNumber(),
                testPassenger.getPassportNumber(),
                "ECONOMY"
            );
        });
        
        assertNotNull(exception, "Exception should be thrown");
        System.out.println("✓ Invalid flight booking test passed!");
    }
    
    @Test
    @DisplayName("Should get booking by ID")
    void testGetBookingById() throws BookingException {
        System.out.println("Testing get booking by ID...");
        
        // Create booking first
        Booking originalBooking = bookingService.createBooking(
            testPassenger.getUserId(),
            testFlight.getFlightId(),
            testPassenger.getName(),
            testPassenger.getEmail(),
            testPassenger.getPhoneNumber(),
            testPassenger.getPassportNumber(),
            "BUSINESS"
        );
        
        // Retrieve booking
        Booking retrievedBooking = bookingService.getBookingById(originalBooking.getBookingId());

        assertNotNull(retrievedBooking, "Retrieved booking should not be null");
        assertEquals(originalBooking.getBookingId(), retrievedBooking.getBookingId(), "Booking ID should match");
        assertEquals("BUSINESS", retrievedBooking.getTravelClass(), "Seat class should match");
        
        System.out.println("✓ Get booking by ID test passed!");
    }
    
    @Test
    @DisplayName("Should return null for invalid booking ID")
    void testGetBookingByInvalidId() {
        System.out.println("Testing get booking by invalid ID...");
        
        Booking booking = bookingService.getBookingById("INVALID_BOOKING_ID");
        assertNull(booking, "Should return null for invalid ID");
        
        System.out.println("✓ Invalid booking ID test passed!");
    }
    
    @Test
    @DisplayName("Should cancel booking")
    void testCancelBooking() throws BookingException {
        System.out.println("Testing cancel booking...");
        
        // Create booking first
        Booking booking = bookingService.createBooking(
            testPassenger.getUserId(),
            testFlight.getFlightId(),
            testPassenger.getName(),
            testPassenger.getEmail(),
            testPassenger.getPhoneNumber(),
            testPassenger.getPassportNumber(),
            "ECONOMY"
        );
        
        // Cancel booking
        boolean cancelled = bookingService.cancelBooking(booking.getBookingId());
        assertTrue(cancelled, "Booking should be cancelled successfully");
        
        // Verify cancellation
        Booking cancelledBooking = bookingService.getBookingById(booking.getBookingId());
        assertEquals("CANCELLED", cancelledBooking.getStatus(), "Status should be CANCELLED");
        
        System.out.println("✓ Cancel booking test passed!");
    }
}