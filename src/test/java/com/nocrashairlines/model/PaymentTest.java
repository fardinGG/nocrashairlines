package com.nocrashairlines.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Test class for Payment model
 * Tests FR-5, NFR-1 to NFR-5 (Payment Processing, Security, Methods, Verification, Refund, Fraud Detection)
 */
@DisplayName("Payment Model Tests")
class PaymentTest {
    
    private Payment payment;
    
    @BeforeEach
    void setUp() {
        payment = new Payment(
            "PAY-001",
            "BK-001",
            "PASS-001",
            299.99,
            "CREDIT_CARD"
        );
        payment.setCardLastFourDigits("1234");
    }
    
    @Test
    @DisplayName("Should create payment with valid data")
    void testPaymentCreation() {
        assertNotNull(payment, "Payment should not be null");
        assertEquals("PAY-001", payment.getPaymentId(), "Payment ID should match");
        assertEquals("BK-001", payment.getBookingId(), "Booking ID should match");
        assertEquals("PASS-001", payment.getPassengerId(), "Passenger ID should match");
        assertEquals(299.99, payment.getAmount(), 0.01, "Amount should match");
        assertEquals("CREDIT_CARD", payment.getPaymentMethod(), "Payment method should match");
        assertEquals("PENDING", payment.getStatus(), "Initial status should be PENDING");
        assertFalse(payment.isFraudDetected(), "Fraud should not be detected initially");
        assertNotNull(payment.getPaymentDate(), "Payment date should be set");
    }
    
    @Test
    @DisplayName("Should mark payment as success")
    void testMarkAsSuccess() {
        assertEquals("PENDING", payment.getStatus(), "Initial status should be PENDING");
        
        String transactionRef = "TXN-ABC123";
        payment.markAsSuccess(transactionRef);
        
        assertEquals("SUCCESS", payment.getStatus(), "Status should be SUCCESS");
        assertEquals(transactionRef, payment.getTransactionReference(), "Transaction reference should match");
        assertTrue(payment.isSuccessful(), "Payment should be successful");
    }
    
    @Test
    @DisplayName("Should mark payment as failed")
    void testMarkAsFailed() {
        payment.markAsFailed();
        
        assertEquals("FAILED", payment.getStatus(), "Status should be FAILED");
        assertFalse(payment.isSuccessful(), "Payment should not be successful");
    }
    
    @Test
    @DisplayName("Should process refund successfully")
    void testProcessRefund() {
        payment.markAsSuccess("TXN-ABC123");
        assertTrue(payment.canBeRefunded(), "Successful payment should be refundable");
        
        LocalDateTime beforeRefund = LocalDateTime.now();
        String refundReason = "Customer requested cancellation";
        payment.processRefund(refundReason);
        LocalDateTime afterRefund = LocalDateTime.now();
        
        assertEquals("REFUNDED", payment.getStatus(), "Status should be REFUNDED");
        assertEquals(refundReason, payment.getRefundReason(), "Refund reason should match");
        assertNotNull(payment.getRefundDate(), "Refund date should be set");
        assertTrue(payment.getRefundDate().isAfter(beforeRefund.minusSeconds(1)), 
                   "Refund date should be recent");
        assertTrue(payment.getRefundDate().isBefore(afterRefund.plusSeconds(1)), 
                   "Refund date should be recent");
    }
    
    @Test
    @DisplayName("Should check if payment can be refunded")
    void testCanBeRefunded() {
        payment.setStatus("PENDING");
        assertFalse(payment.canBeRefunded(), "PENDING payment should not be refundable");
        
        payment.setStatus("SUCCESS");
        assertTrue(payment.canBeRefunded(), "SUCCESS payment should be refundable");
        
        payment.setStatus("FAILED");
        assertFalse(payment.canBeRefunded(), "FAILED payment should not be refundable");
        
        payment.setStatus("REFUNDED");
        assertFalse(payment.canBeRefunded(), "REFUNDED payment should not be refundable");
    }
    
    @Test
    @DisplayName("Should detect fraud")
    void testFraudDetection() {
        assertFalse(payment.isFraudDetected(), "Fraud should not be detected initially");
        
        payment.setFraudDetected(true);
        
        assertTrue(payment.isFraudDetected(), "Fraud should be detected");
    }
    
    @Test
    @DisplayName("Should support multiple payment methods")
    void testPaymentMethods() {
        payment.setPaymentMethod("DEBIT_CARD");
        assertEquals("DEBIT_CARD", payment.getPaymentMethod(), "Should support DEBIT_CARD");
        
        payment.setPaymentMethod("DIGITAL_WALLET");
        assertEquals("DIGITAL_WALLET", payment.getPaymentMethod(), "Should support DIGITAL_WALLET");
        
        payment.setPaymentMethod("ONLINE_BANKING");
        assertEquals("ONLINE_BANKING", payment.getPaymentMethod(), "Should support ONLINE_BANKING");
    }
    
    @Test
    @DisplayName("Should store card information securely")
    void testCardInformation() {
        assertEquals("1234", payment.getCardLastFourDigits(), "Should store last 4 digits");
        
        payment.setCardLastFourDigits("5678");
        assertEquals("5678", payment.getCardLastFourDigits(), "Should update last 4 digits");
    }
    
    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String result = payment.toString();
        
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("PAY-001"), "Should contain payment ID");
        assertTrue(result.contains("BK-001"), "Should contain booking ID");
        assertTrue(result.contains("299.99"), "Should contain amount");
        assertTrue(result.contains("CREDIT_CARD"), "Should contain payment method");
        assertTrue(result.contains("PENDING"), "Should contain status");
    }
    
    @Test
    @DisplayName("Should handle complete payment lifecycle")
    void testPaymentLifecycle() {
        // Initial state
        assertEquals("PENDING", payment.getStatus());
        
        // Process payment
        payment.markAsSuccess("TXN-XYZ789");
        assertEquals("SUCCESS", payment.getStatus());
        assertTrue(payment.isSuccessful());
        
        // Process refund
        payment.processRefund("Flight cancelled");
        assertEquals("REFUNDED", payment.getStatus());
        assertFalse(payment.canBeRefunded());
    }
}

