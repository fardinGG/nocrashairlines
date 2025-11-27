package com.nocrashairlines.service;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.exception.PaymentException;
import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Flight;
import com.nocrashairlines.model.Passenger;
import com.nocrashairlines.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Payment Service Tests")
class PaymentServiceTest {
    
    private PaymentService paymentService;
    private BookingService bookingService;
    private AuthenticationService authService;
    private AdminService adminService;
    private Passenger testPassenger;
    private Booking testBooking;
    
    @BeforeEach
    void setUp() throws AuthenticationException, BookingException {
        paymentService = new PaymentService();
        bookingService = new BookingService();
        authService = new AuthenticationService();
        adminService = new AdminService();
        System.out.println("Setting up PaymentService test...");
        
        // Create test passenger
        testPassenger = authService.registerPassenger(
            "Payment User", 
            "payment@test.com", 
            "PayTest@123", 
            "+1234567890", 
            "PY123456"
        );
        
        // Create test flight and booking
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0);
        Flight testFlight = adminService.addFlight(
            "PAY101", 
            "Vancouver", 
            "Toronto", 
            tomorrow, 
            tomorrow.plusHours(4), 
            "Boeing 777", 
            200, 
            "E8"
        );
        
        testBooking = bookingService.createBooking(
            testPassenger.getUserId(),
            testFlight.getFlightId(),
            testPassenger.getName(),
            testPassenger.getEmail(),
            testPassenger.getPhoneNumber(),
            testPassenger.getPassportNumber(),
            "ECONOMY"
        );
        
        System.out.println("✓ Test data created: Booking " + testBooking.getBookingId());
    }
    
    @Test
    @DisplayName("Should process payment with credit card")
    void testProcessPaymentCreditCard() throws PaymentException, BookingException {
        System.out.println("Testing credit card payment...");
        
        Payment payment = paymentService.processPayment(
            testBooking.getBookingId(),
            testPassenger.getUserId(),
            "CREDIT_CARD",
            "1234"
        );
        
        assertNotNull(payment, "Payment should not be null");
        assertTrue(payment.isSuccessful(), "Payment should be successful");
        assertEquals("CREDIT_CARD", payment.getPaymentMethod(), "Payment method should match");
        assertNotNull(payment.getTransactionReference(), "Transaction reference should not be null");
        assertEquals(testBooking.getBookingId(), payment.getBookingId(), "Booking ID should match");
        
        System.out.println("✓ Credit card payment test passed!");
    }
    
    @Test
    @DisplayName("Should process payment with debit card")
    void testProcessPaymentDebitCard() throws PaymentException, BookingException {
        System.out.println("Testing debit card payment...");
        
        Payment payment = paymentService.processPayment(
            testBooking.getBookingId(),
            testPassenger.getUserId(),
            "DEBIT_CARD",
            "5678"
        );
        
        assertNotNull(payment, "Payment should not be null");
        assertTrue(payment.isSuccessful(), "Payment should be successful");
        assertEquals("DEBIT_CARD", payment.getPaymentMethod(), "Payment method should match");
        
        System.out.println("✓ Debit card payment test passed!");
    }
    
    @Test
    @DisplayName("Should throw exception for invalid booking ID")
    void testProcessPaymentInvalidBooking() {
        System.out.println("Testing payment with invalid booking...");
        
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(
                "INVALID_BOOKING_ID",
                testPassenger.getUserId(),
                "CREDIT_CARD",
                "1234"
            );
        });
        
        assertNotNull(exception, "Exception should be thrown");
        System.out.println("✓ Invalid booking payment test passed!");
    }
    
    @Test
    @DisplayName("Should get payment by booking ID")
    void testGetPaymentByBookingId() throws PaymentException, BookingException {
        System.out.println("Testing get payment by booking ID...");
        
        // Process payment first
        Payment originalPayment = paymentService.processPayment(
            testBooking.getBookingId(),
            testPassenger.getUserId(),
            "DEBIT_CARD",
            "9999"
        );
        
        // Retrieve payment
        Payment retrievedPayment = paymentService.getPaymentByBookingId(testBooking.getBookingId());
        
        assertNotNull(retrievedPayment, "Retrieved payment should not be null");
        assertEquals(originalPayment.getTransactionReference(), 
                    retrievedPayment.getTransactionReference(), "Transaction reference should match");
        assertEquals("DEBIT_CARD", retrievedPayment.getPaymentMethod(), "Payment method should match");
        
        System.out.println("✓ Get payment by booking ID test passed!");
    }
    
    @Test
    @DisplayName("Should process refund")
    void testProcessRefund() throws PaymentException, BookingException {
        System.out.println("Testing refund processing...");
        
        // Process payment first
        paymentService.processPayment(
            testBooking.getBookingId(),
            testPassenger.getUserId(),
            "CREDIT_CARD",
            "1234"
        );
        
        // Process refund
        Payment refund = paymentService.processRefund(
            testBooking.getBookingId(),
            "Customer requested cancellation"
        );
        
        assertNotNull(refund, "Refund should not be null");
        assertTrue(refund.isSuccessful(), "Refund should be successful");
        assertEquals("REFUND", refund.getPaymentMethod(), "Payment method should be REFUND");
        
        System.out.println("✓ Refund processing test passed!");
    }
}