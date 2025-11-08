package com.nocrashairlines.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a passenger in the system.
 * Supports FR-1 through FR-10 (Passenger-related functional requirements)
 */
public class Passenger extends UserAccount {
    private String passportNumber;
    private String address;
    private List<String> bookingIds;
    private String preferredClass; // Economy, Business, First Class

    public Passenger() {
        super();
        this.bookingIds = new ArrayList<>();
    }

    public Passenger(String userId, String name, String email, String password, 
                     String phoneNumber, String passportNumber) {
        super(userId, name, email, password, phoneNumber);
        this.passportNumber = passportNumber;
        this.bookingIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getBookingIds() {
        return bookingIds;
    }

    public void setBookingIds(List<String> bookingIds) {
        this.bookingIds = bookingIds;
    }

    public void addBooking(String bookingId) {
        this.bookingIds.add(bookingId);
    }

    public void removeBooking(String bookingId) {
        this.bookingIds.remove(bookingId);
    }

    public String getPreferredClass() {
        return preferredClass;
    }

    public void setPreferredClass(String preferredClass) {
        this.preferredClass = preferredClass;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", bookingCount=" + bookingIds.size() +
                '}';
    }
}

