package com.nocrashairlines.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a payment transaction in the system.
 * Supports FR-5, FR-23, FR-26, FR-27, FR-28, FR-29 (Payment processing and storage)
 */
public class Payment {
    private String paymentId;
    private String bookingId;
    private String passengerId;
    private double amount;
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, ONLINE_BANKING
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED
    private LocalDateTime paymentDate;
    private String transactionReference;
    private String cardLastFourDigits;
    private boolean fraudDetected;
    private String refundReason;
    private LocalDateTime refundDate;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = "PENDING";
        this.fraudDetected = false;
    }

    public Payment(String paymentId, String bookingId, String passengerId, 
                   double amount, String paymentMethod) {
        this();
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Business methods
    public void markAsSuccess(String transactionReference) {
        this.status = "SUCCESS";
        this.transactionReference = transactionReference;
    }

    public void markAsFailed() {
        this.status = "FAILED";
    }

    public void processRefund(String reason) {
        this.status = "REFUNDED";
        this.refundReason = reason;
        this.refundDate = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    public boolean canBeRefunded() {
        return "SUCCESS".equals(status);
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public boolean isFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", paymentDate=" + paymentDate +
                ", fraudDetected=" + fraudDetected +
                '}';
    }
}

