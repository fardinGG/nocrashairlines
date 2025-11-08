package com.nocrashairlines.util;

import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Flight;
import java.time.format.DateTimeFormatter;

/**
 * Notification service for sending emails and SMS.
 * Supports FR-9 (Notification and Alerts)
 * In production, this would integrate with actual email/SMS services
 */
public class NotificationService {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    /**
     * Send booking confirmation notification
     */
    public boolean sendBookingConfirmation(Booking booking, Flight flight) {
        String message = buildBookingConfirmationMessage(booking, flight);
        return sendEmail(booking.getPassengerEmail(), "Booking Confirmation", message) &&
               sendSMS(booking.getPassengerPhone(), message);
    }
    
    /**
     * Send cancellation notification
     */
    public boolean sendCancellationNotification(Booking booking) {
        String message = buildCancellationMessage(booking);
        return sendEmail(booking.getPassengerEmail(), "Booking Cancelled", message) &&
               sendSMS(booking.getPassengerPhone(), message);
    }
    
    /**
     * Send rescheduling notification
     */
    public boolean sendReschedulingNotification(Booking booking, Flight newFlight) {
        String message = buildReschedulingMessage(booking, newFlight);
        return sendEmail(booking.getPassengerEmail(), "Booking Rescheduled", message) &&
               sendSMS(booking.getPassengerPhone(), message);
    }
    
    /**
     * Send flight delay notification
     */
    public boolean sendFlightDelayNotification(String email, String phone, Flight flight, int delayMinutes) {
        String message = buildFlightDelayMessage(flight, delayMinutes);
        return sendEmail(email, "Flight Delay Alert", message) &&
               sendSMS(phone, message);
    }
    
    /**
     * Send e-ticket via email
     */
    public boolean sendETicket(Booking booking, Flight flight) {
        String message = buildETicketMessage(booking, flight);
        return sendEmail(booking.getPassengerEmail(), "Your E-Ticket - " + booking.getBookingId(), message);
    }
    
    /**
     * Send password reset notification
     */
    public boolean sendPasswordResetNotification(String email, String resetToken) {
        String message = "Your password reset token is: " + resetToken + 
                        "\nThis token will expire in 1 hour.";
        return sendEmail(email, "Password Reset Request", message);
    }
    
    // Private helper methods
    
    private String buildBookingConfirmationMessage(Booking booking, Flight flight) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking has been confirmed!\n\n" +
            "Booking ID: %s\n" +
            "Flight: %s\n" +
            "From: %s\n" +
            "To: %s\n" +
            "Departure: %s\n" +
            "Seat: %s\n" +
            "Class: %s\n" +
            "Amount Paid: $%.2f\n\n" +
            "Thank you for choosing NoCrash Airlines!\n\n" +
            "Safe travels!",
            booking.getPassengerName(),
            booking.getBookingId(),
            flight.getFlightNumber(),
            flight.getOrigin(),
            flight.getDestination(),
            flight.getDepartureTime().format(DATE_FORMATTER),
            booking.getSeatNumber(),
            booking.getTravelClass(),
            booking.getTotalAmount()
        );
    }
    
    private String buildCancellationMessage(Booking booking) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking (ID: %s) has been cancelled.\n" +
            "A refund will be processed according to our refund policy.\n\n" +
            "Thank you,\n" +
            "NoCrash Airlines",
            booking.getPassengerName(),
            booking.getBookingId()
        );
    }
    
    private String buildReschedulingMessage(Booking booking, Flight newFlight) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking (ID: %s) has been rescheduled.\n\n" +
            "New Flight Details:\n" +
            "Flight: %s\n" +
            "From: %s\n" +
            "To: %s\n" +
            "Departure: %s\n\n" +
            "Thank you,\n" +
            "NoCrash Airlines",
            booking.getPassengerName(),
            booking.getBookingId(),
            newFlight.getFlightNumber(),
            newFlight.getOrigin(),
            newFlight.getDestination(),
            newFlight.getDepartureTime().format(DATE_FORMATTER)
        );
    }
    
    private String buildFlightDelayMessage(Flight flight, int delayMinutes) {
        return String.format(
            "FLIGHT DELAY ALERT\n\n" +
            "Flight %s is delayed by %d minutes.\n" +
            "New departure time will be announced soon.\n\n" +
            "We apologize for the inconvenience.\n" +
            "NoCrash Airlines",
            flight.getFlightNumber(),
            delayMinutes
        );
    }
    
    private String buildETicketMessage(Booking booking, Flight flight) {
        return String.format(
            "========== E-TICKET ==========\n\n" +
            "Booking ID: %s\n" +
            "Passenger: %s\n" +
            "Passport: %s\n\n" +
            "Flight: %s\n" +
            "From: %s\n" +
            "To: %s\n" +
            "Departure: %s\n" +
            "Arrival: %s\n" +
            "Gate: %s\n\n" +
            "Seat: %s\n" +
            "Class: %s\n\n" +
            "Please arrive at the airport at least 2 hours before departure.\n\n" +
            "NoCrash Airlines\n" +
            "==============================",
            booking.getBookingId(),
            booking.getPassengerName(),
            booking.getPassportNumber(),
            flight.getFlightNumber(),
            flight.getOrigin(),
            flight.getDestination(),
            flight.getDepartureTime().format(DATE_FORMATTER),
            flight.getArrivalTime().format(DATE_FORMATTER),
            flight.getGate() != null ? flight.getGate() : "TBA",
            booking.getSeatNumber(),
            booking.getTravelClass()
        );
    }
    
    /**
     * Mock email sending (in production, integrate with actual email service)
     */
    private boolean sendEmail(String to, String subject, String body) {
        System.out.println("=== EMAIL ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("=============\n");
        return true; // Simulate success
    }
    
    /**
     * Mock SMS sending (in production, integrate with actual SMS service)
     */
    private boolean sendSMS(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        System.out.println("=== SMS ===");
        System.out.println("To: " + phoneNumber);
        System.out.println("Message: " + message.substring(0, Math.min(160, message.length())));
        System.out.println("===========\n");
        return true; // Simulate success
    }
}

