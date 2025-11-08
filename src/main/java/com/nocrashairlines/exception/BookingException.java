package com.nocrashairlines.exception;

/**
 * Exception thrown when booking operations fail
 */
public class BookingException extends SystemException {
    public BookingException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BookingException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

