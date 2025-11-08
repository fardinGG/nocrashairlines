package com.nocrashairlines.payment;

/**
 * Represents the result of a payment operation
 */
public class PaymentResult {
    private boolean success;
    private String transactionReference;
    private String message;
    private String errorCode;

    public PaymentResult(boolean success, String transactionReference, String message) {
        this.success = success;
        this.transactionReference = transactionReference;
        this.message = message;
    }

    public PaymentResult(boolean success, String message) {
        this(success, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", transactionReference='" + transactionReference + '\'' +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}

