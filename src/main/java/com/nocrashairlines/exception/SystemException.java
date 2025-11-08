package com.nocrashairlines.exception;

/**
 * Base exception class for the flight ticket system.
 * Supports NFR-5 (Error Handling)
 */
public class SystemException extends Exception {
    private final String errorCode;
    private final long timestamp;

    public SystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public SystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s (timestamp: %d)", 
            errorCode, getClass().getSimpleName(), getMessage(), timestamp);
    }
}

