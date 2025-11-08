package com.nocrashairlines.model;

/**
 * Represents airline staff in the system.
 * Supports FR-16 through FR-20 (Airline Staff-related functional requirements)
 */
public class AirlineStaff extends UserAccount {
    private String staffId;
    private String role; // CHECK_IN_AGENT, BOARDING_AGENT, GROUND_STAFF
    private String assignedGate;

    public AirlineStaff() {
        super();
    }

    public AirlineStaff(String userId, String name, String email, String password, 
                        String phoneNumber, String staffId, String role) {
        super(userId, name, email, password, phoneNumber);
        this.staffId = staffId;
        this.role = role;
    }

    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAssignedGate() {
        return assignedGate;
    }

    public void setAssignedGate(String assignedGate) {
        this.assignedGate = assignedGate;
    }

    @Override
    public String toString() {
        return "AirlineStaff{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", staffId='" + staffId + '\'' +
                ", role='" + role + '\'' +
                ", assignedGate='" + assignedGate + '\'' +
                '}';
    }
}

