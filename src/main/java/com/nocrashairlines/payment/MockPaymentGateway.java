package com.nocrashairlines.payment;

import com.nocrashairlines.model.Payment;
import java.util.*;

/**
8 * Supports FR-26 through FR-30 (Payment Gateway functional requirements)
 */
public class MockPaymentGateway implements PaymentGateway {
    
    private static final Set<String> SUPPORTED_PAYMENT_METHODS = new HashSet<>(Arrays.asList(
        "CREDIT_CARD", "DEBIT_CARD", "DIGITAL_WALLET", "ONLINE_BANKING"
    ));
    
    private final Map<String, String> processedTransactions;
    private final Random random;
    
    public MockPaymentGateway() {
        this.processedTransactions = new HashMap<>();
        this.random = new Random();
    }

    @Override
    public PaymentResult processPayment(Payment payment) {
        // FR-26: Secure Payment Processing
        if (payment == null) {
            return new PaymentResult(false, "Payment object is null");
        }
        
        // FR-27: Support Multiple Payment Methods
        if (!isPaymentMethodSupported(payment.getPaymentMethod())) {
            return new PaymentResult(false, "Payment method not supported: " + payment.getPaymentMethod());
        }
        
        // FR-30: Fraud Detection
        if (detectFraud(payment)) {
            payment.setFraudDetected(true);
            return new PaymentResult(false, "Fraudulent transaction detected");
        }
        
        // Simulate payment processing
        try {
            // Simulate network delay
            Thread.sleep(100);
            
            // 95% success rate for simulation
            boolean success = random.nextInt(100) < 95;
            
            if (success) {
                // FR-28: Transaction Verification
                String transactionRef = generateTransactionReference();
                processedTransactions.put(transactionRef, payment.getPaymentId());
                payment.markAsSuccess(transactionRef);
                return new PaymentResult(true, transactionRef, "Payment processed successfully");
            } else {
                payment.markAsFailed();
                return new PaymentResult(false, "Payment processing failed");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false, "Payment processing interrupted");
        }
    }

    @Override
    public boolean verifyTransaction(String transactionReference) {
        // FR-28: Transaction Verification
        return processedTransactions.containsKey(transactionReference);
    }

    @Override
    public PaymentResult processRefund(Payment payment, String reason) {
        // FR-29: Refund Processing
        if (payment == null) {
            return new PaymentResult(false, "Payment object is null");
        }
        
        if (!payment.canBeRefunded()) {
            return new PaymentResult(false, "Payment cannot be refunded. Status: " + payment.getStatus());
        }
        
        if (payment.getTransactionReference() == null || 
            !verifyTransaction(payment.getTransactionReference())) {
            return new PaymentResult(false, "Invalid transaction reference");
        }
        
        try {
            // Simulate refund processing
            Thread.sleep(100);
            
            String refundRef = "REF-" + generateTransactionReference();
            payment.processRefund(reason);
            
            return new PaymentResult(true, refundRef, "Refund processed successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false, "Refund processing interrupted");
        }
    }

    @Override
    public boolean detectFraud(Payment payment) {
        // FR-30: Fraud Detection
        if (payment == null) {
            return false;
        }
        
        // Simple fraud detection rules (in production, this would be more sophisticated)
        
        // Rule 1: Amount too high (over $50,000)
        if (payment.getAmount() > 50000) {
            return true;
        }
        
        // Rule 2: Amount is exactly $0
        if (payment.getAmount() <= 0) {
            return true;
        }
        
        // Rule 3: Missing payment method
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().isEmpty()) {
            return true;
        }
        
        // Rule 4: Simulate random fraud detection (1% chance)
        return random.nextInt(100) < 1;
    }

    @Override
    public boolean isPaymentMethodSupported(String paymentMethod) {
        // FR-27: Support Multiple Payment Methods
        return paymentMethod != null && SUPPORTED_PAYMENT_METHODS.contains(paymentMethod);
    }
    
    /**
     * Generate a unique transaction reference
     */
    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + random.nextInt(10000);
    }
    
    /**
     * Get all processed transactions (for testing/debugging)
     */
    public Map<String, String> getProcessedTransactions() {
        return new HashMap<>(processedTransactions);
    }
    
    /**
     * Clear all processed transactions (for testing)
     */
    public void clearTransactions() {
        processedTransactions.clear();
    }
}

