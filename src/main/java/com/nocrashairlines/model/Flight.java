package com.nocrashairlines.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a flight in the system.
 * Supports FR-3, FR-12, FR-13, FR-21 (Flight search, management, and storage)
 */
public class Flight {
    private String flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String aircraftType;
    private int totalSeats;
    private int availableSeats;
    private Map<String, Double> classPrices; // Economy, Business, First Class
    private String status; // SCHEDULED, DELAYED, CANCELLED, DEPARTED, ARRIVED
    private String gate;

    public Flight() {
        this.classPrices = new HashMap<>();
        this.status = "SCHEDULED";
    }

    public Flight(String flightId, String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, int totalSeats) {
        this();
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    // Business methods
    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public boolean reserveSeat() {
        if (hasAvailableSeats()) {
            availableSeats--;
            return true;
        }
        return false;
    }

    public void releaseSeat() {
        if (availableSeats < totalSeats) {
            availableSeats++;
        }
    }

    public long getDurationInMinutes() {
        if (departureTime != null && arrivalTime != null) {
            return java.time.Duration.between(departureTime, arrivalTime).toMinutes();
        }
        return 0;
    }

    // Getters and Setters
    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Map<String, Double> getClassPrices() {
        return classPrices;
    }

    public void setClassPrices(Map<String, Double> classPrices) {
        this.classPrices = classPrices;
    }

    public void setClassPrice(String travelClass, double price) {
        this.classPrices.put(travelClass, price);
    }

    public Double getClassPrice(String travelClass) {
        return this.classPrices.get(travelClass);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(flightId, flight.flightId) && 
               Objects.equals(flightNumber, flight.flightNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, flightNumber);
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", availableSeats=" + availableSeats + "/" + totalSeats +
                ", status='" + status + '\'' +
                ", gate='" + gate + '\'' +
                '}';
    }
}

