package com.nocrashairlines.service;

import com.nocrashairlines.database.SystemDatabase;
import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.exception.PaymentException;
import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Payment;
import com.nocrashairlines.payment.MockPaymentGateway;
import com.nocrashairlines.payment.PaymentGateway;
import com.nocrashairlines.payment.PaymentResult;
import java.util.UUID;

/**
 * Service for payment operations.
 * Supports UC-3 (Make Payment)
 * Supports FR-5, FR-29 (Payment and Refund Processing)
 */
public class PaymentService {
    
    private final SystemDatabase database;
    private final PaymentGateway paymentGateway;
    private final BookingService bookingService;
    
    public PaymentService() {
        this.database = SystemDatabase.getInstance();
        this.paymentGateway = new MockPaymentGateway();
        this.bookingService = new BookingService();
    }
    
    /**
     * Process payment for a booking
     * UC-3: Make Payment
     * FR-5: Online Payment
     */
    public Payment processPayment(String bookingId, String passengerId, 
                                 String paymentMethod, String cardLastFourDigits) 
            throws PaymentException, BookingException {
        
        // Validate inputs
        if (bookingId == null || passengerId == null || paymentMethod == null) {
            throw new PaymentException("INVALID_INPUT", "Required fields are missing");
        }
        
        // Get booking
        Booking booking = database.getBookingById(bookingId);
        if (booking == null) {
            throw new PaymentException("BOOKING_NOT_FOUND", "Booking not found");
        }
        
        // Verify booking belongs to passenger
        if (!booking.getPassengerId().equals(passengerId)) {
            throw new PaymentException("UNAUTHORIZED", "Booking does not belong to this passenger");
        }
        
        // Check if booking is already paid
        if ("CONFIRMED".equals(booking.getStatus())) {
            throw new PaymentException("ALREADY_PAID", "Booking is already confirmed and paid");
        }
        
        // Check if payment method is supported
        if (!paymentGateway.isPaymentMethodSupported(paymentMethod)) {
            throw new PaymentException("UNSUPPORTED_METHOD", 
                "Payment method not supported: " + paymentMethod);
        }
        
        // Create payment record
        String paymentId = generatePaymentId();
        Payment payment = new Payment(paymentId, bookingId, passengerId, 
                                     booking.getTotalAmount(), paymentMethod);
        payment.setCardLastFourDigits(cardLastFourDigits);
        
        // Process payment through gateway
        PaymentResult result = paymentGateway.processPayment(payment);
        
        if (result.isSuccess()) {
            // Payment successful
            payment.markAsSuccess(result.getTransactionReference());
            
            // Save payment
            if (database.savePayment(payment)) {
                // Update booking with payment ID and confirm
                booking.setPaymentId(paymentId);
                bookingService.confirmBooking(bookingId);
                
                return payment;
            } else {
                throw new PaymentException("PAYMENT_SAVE_FAILED", "Failed to save payment record");
            }
        } else {
            // Payment failed
            payment.markAsFailed();
            database.savePayment(payment);
            
            throw new PaymentException("PAYMENT_FAILED", result.getMessage());
        }
    }
    
    /**
     * Process refund for a cancelled booking
     * FR-29: Refund Processing
     */
    public Payment processRefund(String bookingId, String reason) throws PaymentException {
        // Get booking
        Booking booking = database.getBookingById(bookingId);
        if (booking == null) {
            throw new PaymentException("BOOKING_NOT_FOUND", "Booking not found");
        }
        
        // Check if booking is cancelled
        if (!"CANCELLED".equals(booking.getStatus())) {
            throw new PaymentException("BOOKING_NOT_CANCELLED", 
                "Booking must be cancelled before refund can be processed");
        }
        
        // Get original payment
        Payment originalPayment = database.getPaymentByBookingId(bookingId);
        if (originalPayment == null) {
            throw new PaymentException("PAYMENT_NOT_FOUND", "Original payment not found");
        }
        
        if (!originalPayment.canBeRefunded()) {
            throw new PaymentException("CANNOT_REFUND", 
                "Payment cannot be refunded. Status: " + originalPayment.getStatus());
        }
        
        // Process refund through gateway
        PaymentResult result = paymentGateway.processRefund(originalPayment, reason);
        
        if (result.isSuccess()) {
            // Update payment record
            originalPayment.processRefund(reason);
            database.updatePayment(originalPayment);
            
            return originalPayment;
        } else {
            throw new PaymentException("REFUND_FAILED", result.getMessage());
        }
    }
    
    /**
     * Get payment by ID
     */
    public Payment getPaymentById(String paymentId) {
        return database.getPaymentById(paymentId);
    }
    
    /**
     * Get payment by booking ID
     */
    public Payment getPaymentByBookingId(String bookingId) {
        return database.getPaymentByBookingId(bookingId);
    }
    
    /**
     * Verify a transaction
     */
    public boolean verifyTransaction(String transactionReference) {
        return paymentGateway.verifyTransaction(transactionReference);
    }

    /**
     * Get all payments (Admin only)
     */
    public java.util.List<Payment> getAllPayments() {
        return database.getAllPayments();
    }

    // Helper methods

    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

