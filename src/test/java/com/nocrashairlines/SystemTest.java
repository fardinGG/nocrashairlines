package com.nocrashairlines;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.exception.PaymentException;
import com.nocrashairlines.model.*;
import com.nocrashairlines.service.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * System test to verify all use cases work correctly
 */
public class SystemTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  NoCrash Airlines System Test");
        System.out.println("========================================\n");
        
        try {
            testAllUseCases();
            System.out.println("\n✅ ALL TESTS PASSED!");
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testAllUseCases() throws Exception {
        AuthenticationService authService = new AuthenticationService();
        FlightService flightService = new FlightService();
        BookingService bookingService = new BookingService();
        PaymentService paymentService = new PaymentService();
        AdminService adminService = new AdminService();
        
        // Test UC-7: Passenger Registration
        System.out.println("Testing UC-7: Passenger Registration...");
        Passenger passenger = authService.registerPassenger(
            "John Doe", 
            "john.doe@example.com", 
            "Password@123", 
            "+1234567890", 
            "AB123456"
        );
        assert passenger != null : "Passenger registration failed";
        System.out.println("✓ Passenger registered: " + passenger.getUserId());
        
        // Test UC-8: Passenger Login
        System.out.println("\nTesting UC-8: Passenger Login...");
        Passenger loggedInPassenger = authService.loginPassenger(
            "john.doe@example.com", 
            "Password@123"
        );
        assert loggedInPassenger != null : "Passenger login failed";
        System.out.println("✓ Passenger logged in: " + loggedInPassenger.getName());
        
        // Test UC-9: Admin Manage Flights
        System.out.println("\nTesting UC-9: Admin Manage Flights...");
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Flight flight = adminService.addFlight(
            "TEST101", 
            "Toronto", 
            "Vancouver",
            tomorrow, 
            tomorrow.plusHours(5), 
            "Boeing 737", 
            180, 
            "A12"
        );
        assert flight != null : "Flight creation failed";
        System.out.println("✓ Flight added: " + flight.getFlightNumber());
        
        // Test UC-1: Search Flights
        System.out.println("\nTesting UC-1: Search Flights...");
        List<Flight> flights = flightService.searchFlights("Toronto", "Vancouver", tomorrow);
        assert !flights.isEmpty() : "Flight search failed";
        System.out.println("✓ Found " + flights.size() + " flight(s)");
        
        // Test UC-2: Book Ticket
        System.out.println("\nTesting UC-2: Book Ticket...");
        Booking booking = bookingService.createBooking(
            passenger.getUserId(),
            flight.getFlightId(),
            passenger.getName(),
            passenger.getEmail(),
            passenger.getPhoneNumber(),
            passenger.getPassportNumber(),
            "ECONOMY"
        );
        assert booking != null : "Booking creation failed";
        System.out.println("✓ Booking created: " + booking.getBookingId());
        
        // Test UC-3: Make Payment
        System.out.println("\nTesting UC-3: Make Payment...");
        Payment payment = paymentService.processPayment(
            booking.getBookingId(),
            passenger.getUserId(),
            "CREDIT_CARD",
            "1234"
        );
        assert payment != null : "Payment processing failed";
        assert payment.isSuccessful() : "Payment was not successful";
        System.out.println("✓ Payment processed: " + payment.getTransactionReference());
        
        // Test UC-6: View Booking
        System.out.println("\nTesting UC-6: View Booking...");
        Booking retrievedBooking = bookingService.getBookingById(booking.getBookingId());
        assert retrievedBooking != null : "Booking retrieval failed";
        assert "CONFIRMED".equals(retrievedBooking.getStatus()) : "Booking not confirmed";
        System.out.println("✓ Booking retrieved: " + retrievedBooking.getBookingId() + 
                          " (Status: " + retrievedBooking.getStatus() + ")");
        
        // Test UC-5: Reschedule Flight
        System.out.println("\nTesting UC-5: Reschedule Flight...");
        Flight newFlight = adminService.addFlight(
            "TEST202",
            "Toronto",
            "Vancouver",
            tomorrow.plusDays(1),
            tomorrow.plusDays(1).plusHours(5),
            "Boeing 737",
            180,
            "B5"
        );
        boolean rescheduled = bookingService.rescheduleBooking(
            booking.getBookingId(),
            newFlight.getFlightId()
        );
        assert rescheduled : "Booking rescheduling failed";
        System.out.println("✓ Booking rescheduled to flight: " + newFlight.getFlightNumber());
        
        // Test UC-4: Cancel Booking (create a new booking for cancellation)
        System.out.println("\nTesting UC-4: Cancel Booking...");
        Booking cancelBooking = bookingService.createBooking(
            passenger.getUserId(),
            flight.getFlightId(),
            passenger.getName(),
            passenger.getEmail(),
            passenger.getPhoneNumber(),
            passenger.getPassportNumber(),
            "BUSINESS"
        );
        Payment cancelPayment = paymentService.processPayment(
            cancelBooking.getBookingId(),
            passenger.getUserId(),
            "DEBIT_CARD",
            "5678"
        );

        boolean cancelled = bookingService.cancelBooking(cancelBooking.getBookingId());
        assert cancelled : "Booking cancellation failed";

        // Process refund
        Payment refund = paymentService.processRefund(
            cancelBooking.getBookingId(),
            "Customer requested cancellation"
        );
        assert refund != null : "Refund processing failed";
        System.out.println("✓ Booking cancelled and refund processed");
        
        // Test UC-10: Generate Reports
        System.out.println("\nTesting UC-10: Generate Reports...");
        String salesReport = adminService.generateDailySalesReport(LocalDateTime.now());
        assert salesReport != null && !salesReport.isEmpty() : "Sales report generation failed";
        System.out.println("✓ Daily sales report generated");
        
        String trendsReport = adminService.generatePassengerTrendsReport();
        assert trendsReport != null && !trendsReport.isEmpty() : "Trends report generation failed";
        System.out.println("✓ Passenger trends report generated");
        
        String routesReport = adminService.generateMostBookedRoutesReport();
        assert routesReport != null && !routesReport.isEmpty() : "Routes report generation failed";
        System.out.println("✓ Most booked routes report generated");
        
        String statsReport = adminService.generateSystemStatisticsReport();
        assert statsReport != null && !statsReport.isEmpty() : "Statistics report generation failed";
        System.out.println("✓ System statistics report generated");
        
        // Test Password Policy (NFR-6)
        System.out.println("\nTesting NFR-6: Password Policy...");
        try {
            authService.registerPassenger(
                "Test User",
                "test@example.com",
                "weak",  // Weak password
                "+1234567890",
                "CD789012"
            );
            throw new AssertionError("Weak password should have been rejected");
        } catch (AuthenticationException e) {
            System.out.println("✓ Weak password correctly rejected: " + e.getMessage());
        }
        
        // Test Account Lockout (NFR-6)
        System.out.println("\nTesting NFR-6: Account Lockout...");
        Passenger testPassenger = authService.registerPassenger(
            "Lock Test",
            "locktest@example.com",
            "LockTest@123",
            "+1234567890",
            "EF345678"
        );
        
        // Try 5 failed login attempts
        for (int i = 0; i < 5; i++) {
            try {
                authService.loginPassenger("locktest@example.com", "wrongpassword");
            } catch (AuthenticationException e) {
                // Expected
            }
        }
        
        // 6th attempt should result in account locked
        try {
            authService.loginPassenger("locktest@example.com", "LockTest@123");
            throw new AssertionError("Account should be locked");
        } catch (AuthenticationException e) {
            assert e.getMessage().contains("locked") : "Account should be locked";
            System.out.println("✓ Account correctly locked after 5 failed attempts");
        }
        
        System.out.println("\n========================================");
        System.out.println("  Test Summary");
        System.out.println("========================================");
        System.out.println("✓ UC-1: Search Flights - PASSED");
        System.out.println("✓ UC-2: Book Ticket - PASSED");
        System.out.println("✓ UC-3: Make Payment - PASSED");
        System.out.println("✓ UC-4: Cancel Booking - PASSED");
        System.out.println("✓ UC-5: Reschedule Flight - PASSED");
        System.out.println("✓ UC-6: View Booking - PASSED");
        System.out.println("✓ UC-7: Passenger Registration - PASSED");
        System.out.println("✓ UC-8: Passenger Login - PASSED");
        System.out.println("✓ UC-9: Admin Manage Flights - PASSED");
        System.out.println("✓ UC-10: Generate Reports - PASSED");
        System.out.println("✓ NFR-6: Password Policy - PASSED");
        System.out.println("✓ NFR-6: Account Lockout - PASSED");
    }
}

