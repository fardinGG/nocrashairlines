package com.nocrashairlines.payment;

import com.nocrashairlines.model.Payment;

/**
 * Interface for payment gateway operations.
 * Supports FR-26 through FR-30 (Payment Gateway-related functional requirements)
 */
public interface PaymentGateway {
    
    /**
     * Process  payment transaction
     * @param payment Payment object containing transaction details
     * @return PaymentResult containing success status and transaction reference
     */
    PaymentResult processPayment(Payment payment);
    
    /**
     * Verify a transaction's authenticity
     * @param transactionReference Transaction reference to verify
     * @return true if transaction is valid, false otherwise
     */
    boolean verifyTransaction(String transactionReference);
    
    /**
     * Process a refund for a successful payment
     * @param payment Original payment to refund
     * @param reason Reason for refund
     * @return PaymentResult containing refund status
     */
    PaymentResult processRefund(Payment payment, String reason);
    
    /**
     * Detect potential fraud in a payment
     * @param payment Payment to check for fraud
     * @return true if fraud is detected, false otherwise
     */
    boolean detectFraud(Payment payment);
    
    /**
     * Check if a payment method is supported
     * @param paymentMethod Payment method to check
     * @return true if supported, false otherwise
     */
    boolean isPaymentMethodSupported(String paymentMethod);
}

