package com.nocrashairlines.model;

/**
 * Represents an administrator in the system.
 * Supports FR-11 through FR-15 (Admin-related functional requirements)
 */
public class Admin extends UserAccount {
    private String adminLevel; // SUPER_ADMIN, ADMIN, MODERATOR
    private String department;

    public Admin() {
        super();
    }

    public Admin(String userId, String name, String email, String password, 
                 String phoneNumber, String adminLevel) {
        super(userId, name, email, password, phoneNumber);
        this.adminLevel = adminLevel;
    }

    // Getters and Setters
    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}

