package com.nocrashairlines.service;

import com.nocrashairlines.database.SystemDatabase;
import com.nocrashairlines.model.Flight;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for flight operations.
 * Supports UC-1 (Search Flights) and UC-9 (Admin Manage Flights)
 * Supports FR-3, FR-12, FR-13
 */
public class FlightService {
    
    private final SystemDatabase database;
    
    public FlightService() {
        this.database = SystemDatabase.getInstance();
    }
    
    /**
     * Search for available flights
     * UC-1: Search Flights
     */
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        if (origin == null || destination == null || date == null) {
            throw new IllegalArgumentException("Origin, destination, and date are required");
        }
        
        return database.searchFlights(origin, destination, date);
    }
    
    /**
     * Get flight by ID
     */
    public Flight getFlightById(String flightId) {
        return database.getFlightById(flightId);
    }
    
    /**
     * Get flight by flight number
     */
    public Flight getFlightByNumber(String flightNumber) {
        return database.getFlightByNumber(flightNumber);
    }
    
    /**
     * Get all flights
     */
    public List<Flight> getAllFlights() {
        return database.getAllFlights();
    }
    
    /**
     * Add a new flight (Admin only)
     * UC-9: Admin Manage Flights
     * FR-12: Flight Management
     */
    public Flight addFlight(String flightNumber, String origin, String destination,
                           LocalDateTime departureTime, LocalDateTime arrivalTime,
                           String aircraftType, int totalSeats) {
        
        // Validate inputs
        if (flightNumber == null || origin == null || destination == null) {
            throw new IllegalArgumentException("Flight number, origin, and destination are required");
        }
        
        if (departureTime == null || arrivalTime == null) {
            throw new IllegalArgumentException("Departure and arrival times are required");
        }
        
        if (departureTime.isAfter(arrivalTime)) {
            throw new IllegalArgumentException("Departure time must be before arrival time");
        }
        
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than zero");
        }
        
        // Check if flight number already exists
        if (database.getFlightByNumber(flightNumber) != null) {
            throw new IllegalArgumentException("Flight number already exists");
        }
        
        // Create new flight
        String flightId = UUID.randomUUID().toString();
        Flight flight = new Flight(flightId, flightNumber, origin, destination,
                                  departureTime, arrivalTime, totalSeats);
        flight.setAircraftType(aircraftType);
        
        // Save to database
        if (database.saveFlight(flight)) {
            return flight;
        } else {
            throw new RuntimeException("Failed to save flight");
        }
    }
    
    /**
     * Update flight details (Admin only)
     * UC-9: Admin Manage Flights
     */
    public boolean updateFlight(Flight flight) {
        if (flight == null || flight.getFlightId() == null) {
            return false;
        }
        
        return database.updateFlight(flight);
    }
    
    /**
     * Delete a flight (Admin only)
     * UC-9: Admin Manage Flights
     */
    public boolean deleteFlight(String flightId) {
        if (flightId == null) {
            return false;
        }
        
        return database.deleteFlight(flightId);
    }
    
    /**
     * Update flight status
     */
    public boolean updateFlightStatus(String flightId, String status) {
        Flight flight = database.getFlightById(flightId);
        if (flight == null) {
            return false;
        }
        
        flight.setStatus(status);
        return database.updateFlight(flight);
    }
    
    /**
     * Update seat availability
     * FR-13: Seat Inventory Management
     */
    public boolean updateSeatAvailability(String flightId, int availableSeats) {
        Flight flight = database.getFlightById(flightId);
        if (flight == null) {
            return false;
        }
        
        if (availableSeats < 0 || availableSeats > flight.getTotalSeats()) {
            return false;
        }
        
        flight.setAvailableSeats(availableSeats);
        return database.updateFlight(flight);
    }
    
    /**
     * Reserve a seat on a flight
     */
    public boolean reserveSeat(String flightId) {
        Flight flight = database.getFlightById(flightId);
        if (flight == null) {
            return false;
        }
        
        if (flight.reserveSeat()) {
            return database.updateFlight(flight);
        }
        
        return false;
    }
    
    /**
     * Release a seat on a flight
     */
    public boolean releaseSeat(String flightId) {
        Flight flight = database.getFlightById(flightId);
        if (flight == null) {
            return false;
        }
        
        flight.releaseSeat();
        return database.updateFlight(flight);
    }
}

