package com.nocrashairlines.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a flight booking in the system.
 * Supports FR-4, FR-6, FR-7, FR-8, FR-22 (Booking management and storage)
 */
public class Booking {
    private String bookingId;
    private String passengerId;
    private String flightId;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    private String passportNumber;
    private String seatNumber;
    private String travelClass; // ECONOMY, BUSINESS, FIRST_CLASS
    private String status; // PENDING, CONFIRMED, CANCELLED, RESCHEDULED
    private LocalDateTime bookingDate;
    private LocalDateTime lastModified;
    private String paymentId;
    private double totalAmount;
    private boolean checkedIn;
    private String baggageTag;

    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.status = "PENDING";
        this.checkedIn = false;
    }

    public Booking(String bookingId, String passengerId, String flightId, 
                   String passengerName, String travelClass, double totalAmount) {
        this();
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.travelClass = travelClass;
        this.totalAmount = totalAmount;
    }

    // Business methods
    public boolean canBeCancelled() {
        return "CONFIRMED".equals(status) || "PENDING".equals(status);
    }

    public boolean canBeRescheduled() {
        return "CONFIRMED".equals(status);
    }

    public void confirmBooking() {
        this.status = "CONFIRMED";
        this.lastModified = LocalDateTime.now();
    }

    public void cancelBooking() {
        this.status = "CANCELLED";
        this.lastModified = LocalDateTime.now();
    }

    public void rescheduleBooking(String newFlightId) {
        this.flightId = newFlightId;
        this.status = "RESCHEDULED";
        this.lastModified = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getBaggageTag() {
        return baggageTag;
    }

    public void setBaggageTag(String baggageTag) {
        this.baggageTag = baggageTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", flightId='" + flightId + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", travelClass='" + travelClass + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", bookingDate=" + bookingDate +
                '}';
    }
}

