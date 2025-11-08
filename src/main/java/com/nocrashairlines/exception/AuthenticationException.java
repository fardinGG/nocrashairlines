package com.nocrashairlines.exception;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends SystemException {
    public AuthenticationException(String message) {
        super("AUTH_ERROR", message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super("AUTH_ERROR", message, cause);
    }
}

