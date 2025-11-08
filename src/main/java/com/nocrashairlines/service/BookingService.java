package com.nocrashairlines.service;

import com.nocrashairlines.database.SystemDatabase;
import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Flight;
import com.nocrashairlines.model.Passenger;
import com.nocrashairlines.util.NotificationService;
import java.util.List;
import java.util.UUID;

/**
 * Service for booking operations.
 * Supports UC-2 (Book Ticket), UC-4 (Cancel Booking), UC-5 (Reschedule Flight), UC-6 (View Booking)
 * Supports FR-4, FR-6, FR-7, FR-8
 */
public class BookingService {
    
    private final SystemDatabase database;
    private final FlightService flightService;
    private final NotificationService notificationService;
    
    public BookingService() {
        this.database = SystemDatabase.getInstance();
        this.flightService = new FlightService();
        this.notificationService = new NotificationService();
    }
    
    /**
     * Create a new booking
     * UC-2: Book Ticket
     * FR-4: Ticket Booking
     */
    public Booking createBooking(String passengerId, String flightId, String passengerName,
                                String passengerEmail, String passengerPhone, 
                                String passportNumber, String travelClass) 
            throws BookingException {
        
        // Validate inputs
        if (passengerId == null || flightId == null || passengerName == null) {
            throw new BookingException("INVALID_INPUT", "Required fields are missing");
        }
        
        // Get flight
        Flight flight = flightService.getFlightById(flightId);
        if (flight == null) {
            throw new BookingException("FLIGHT_NOT_FOUND", "Flight not found");
        }
        
        // Check if flight has available seats
        if (!flight.hasAvailableSeats()) {
            throw new BookingException("NO_SEATS", "No seats available on this flight");
        }
        
        // Check if flight is scheduled
        if (!"SCHEDULED".equals(flight.getStatus())) {
            throw new BookingException("FLIGHT_NOT_AVAILABLE", 
                "Flight is not available for booking. Status: " + flight.getStatus());
        }
        
        // Get price for travel class
        Double price = flight.getClassPrice(travelClass);
        if (price == null) {
            throw new BookingException("INVALID_CLASS", "Invalid travel class: " + travelClass);
        }
        
        // Create booking
        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, passengerId, flightId, 
                                     passengerName, travelClass, price);
        booking.setPassengerEmail(passengerEmail);
        booking.setPassengerPhone(passengerPhone);
        booking.setPassportNumber(passportNumber);
        
        // Assign seat (simplified - in production, allow passenger to choose)
        String seatNumber = assignSeat(flight, travelClass);
        booking.setSeatNumber(seatNumber);
        
        // Reserve seat on flight
        if (!flightService.reserveSeat(flightId)) {
            throw new BookingException("SEAT_RESERVATION_FAILED", "Failed to reserve seat");
        }
        
        // Save booking
        if (database.saveBooking(booking)) {
            // Update passenger's booking list
            Passenger passenger = database.getPassengerById(passengerId);
            if (passenger != null) {
                passenger.addBooking(bookingId);
                database.updatePassenger(passenger);
            }
            
            return booking;
        } else {
            // Rollback seat reservation
            flightService.releaseSeat(flightId);
            throw new BookingException("BOOKING_FAILED", "Failed to create booking");
        }
    }
    
    /**
     * Confirm a booking after payment
     * FR-8: E-Ticket Generation
     */
    public boolean confirmBooking(String bookingId) throws BookingException {
        Booking booking = database.getBookingById(bookingId);
        if (booking == null) {
            throw new BookingException("BOOKING_NOT_FOUND", "Booking not found");
        }
        
        booking.confirmBooking();
        boolean updated = database.updateBooking(booking);
        
        if (updated) {
            // Send confirmation and e-ticket
            Flight flight = flightService.getFlightById(booking.getFlightId());
            if (flight != null) {
                notificationService.sendBookingConfirmation(booking, flight);
                notificationService.sendETicket(booking, flight);
            }
        }
        
        return updated;
    }
    
    /**
     * Cancel a booking
     * UC-4: Cancel Booking
     * FR-7: Cancel Booking
     */
    public boolean cancelBooking(String bookingId) throws BookingException {
        Booking booking = database.getBookingById(bookingId);
        if (booking == null) {
            throw new BookingException("BOOKING_NOT_FOUND", "Booking not found");
        }
        
        if (!booking.canBeCancelled()) {
            throw new BookingException("CANNOT_CANCEL", 
                "Booking cannot be cancelled. Status: " + booking.getStatus());
        }
        
        // Cancel booking
        booking.cancelBooking();
        boolean updated = database.updateBooking(booking);
        
        if (updated) {
            // Release seat
            flightService.releaseSeat(booking.getFlightId());
            
            // Send cancellation notification
            notificationService.sendCancellationNotification(booking);
            
            // Remove from passenger's booking list
            Passenger passenger = database.getPassengerById(booking.getPassengerId());
            if (passenger != null) {
                passenger.removeBooking(bookingId);
                database.updatePassenger(passenger);
            }
        }
        
        return updated;
    }
    
    /**
     * Reschedule a booking to a new flight
     * UC-5: Reschedule Flight
     * FR-7: Reschedule Booking
     */
    public boolean rescheduleBooking(String bookingId, String newFlightId) 
            throws BookingException {
        
        Booking booking = database.getBookingById(bookingId);
        if (booking == null) {
            throw new BookingException("BOOKING_NOT_FOUND", "Booking not found");
        }
        
        if (!booking.canBeRescheduled()) {
            throw new BookingException("CANNOT_RESCHEDULE", 
                "Booking cannot be rescheduled. Status: " + booking.getStatus());
        }
        
        // Get new flight
        Flight newFlight = flightService.getFlightById(newFlightId);
        if (newFlight == null) {
            throw new BookingException("FLIGHT_NOT_FOUND", "New flight not found");
        }
        
        if (!newFlight.hasAvailableSeats()) {
            throw new BookingException("NO_SEATS", "No seats available on new flight");
        }
        
        // Release seat on old flight
        String oldFlightId = booking.getFlightId();
        flightService.releaseSeat(oldFlightId);
        
        // Reserve seat on new flight
        if (!flightService.reserveSeat(newFlightId)) {
            // Rollback - re-reserve old seat
            flightService.reserveSeat(oldFlightId);
            throw new BookingException("SEAT_RESERVATION_FAILED", 
                "Failed to reserve seat on new flight");
        }
        
        // Update booking
        String oldSeatNumber = booking.getSeatNumber();
        booking.rescheduleBooking(newFlightId);
        
        // Assign new seat
        String newSeatNumber = assignSeat(newFlight, booking.getTravelClass());
        booking.setSeatNumber(newSeatNumber);
        
        boolean updated = database.updateBooking(booking);
        
        if (updated) {
            // Send rescheduling notification
            notificationService.sendReschedulingNotification(booking, newFlight);
        } else {
            // Rollback
            booking.setFlightId(oldFlightId);
            booking.setSeatNumber(oldSeatNumber);
            flightService.releaseSeat(newFlightId);
            flightService.reserveSeat(oldFlightId);
        }
        
        return updated;
    }
    
    /**
     * Get booking by ID
     * UC-6: View Booking
     * FR-6: Booking Management
     */
    public Booking getBookingById(String bookingId) {
        return database.getBookingById(bookingId);
    }
    
    /**
     * Get all bookings for a passenger
     * UC-6: View Booking
     */
    public List<Booking> getBookingsByPassengerId(String passengerId) {
        return database.getBookingsByPassengerId(passengerId);
    }
    
    /**
     * Get all bookings for a flight
     * FR-14: Booking Oversight
     */
    public List<Booking> getBookingsByFlightId(String flightId) {
        return database.getBookingsByFlightId(flightId);
    }
    
    /**
     * Get all bookings (Admin only)
     * FR-14: Booking Oversight
     */
    public List<Booking> getAllBookings() {
        return database.getAllBookings();
    }
    
    /**
     * Check in a passenger
     * FR-19: Handle Baggage Check-In
     */
    public boolean checkInPassenger(String bookingId, String baggageTag) {
        Booking booking = database.getBookingById(bookingId);
        if (booking == null || !"CONFIRMED".equals(booking.getStatus())) {
            return false;
        }
        
        booking.setCheckedIn(true);
        booking.setBaggageTag(baggageTag);
        return database.updateBooking(booking);
    }
    
    // Helper methods
    
    private String generateBookingId() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String assignSeat(Flight flight, String travelClass) {
        // Simplified seat assignment
        // In production, this would check actual seat availability
        int seatRow = (int)(Math.random() * 30) + 1;
        char seatLetter = (char)('A' + (int)(Math.random() * 6));
        return seatRow + String.valueOf(seatLetter);
    }
}

