package com.nocrashairlines.exception;

/**
 * Exception thrown when payment operations fail
 */
public class PaymentException extends SystemException {
    public PaymentException(String errorCode, String message) {
        super(errorCode, message);
    }

    public PaymentException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

