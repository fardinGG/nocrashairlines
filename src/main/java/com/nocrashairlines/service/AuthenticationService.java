package com.nocrashairlines.service;

import com.nocrashairlines.database.SystemDatabase;
import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.model.*;
import com.nocrashairlines.util.PasswordValidator;
import com.nocrashairlines.util.ValidationResult;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Service for user authentication and registration.
 * Supports UC-7 (Passenger Registration) and UC-8 (Passenger Login)
 * Supports FR-1, FR-2, FR-11 and NFR-6 (Password Policy)
 */
public class AuthenticationService {
    
    private final SystemDatabase database;
    
    public AuthenticationService() {
        this.database = SystemDatabase.getInstance();
    }
    
    /**
     * Register a new passenger
     * UC-7: Passenger Registration
     */
    public Passenger registerPassenger(String name, String email, String password, 
                                      String phoneNumber, String passportNumber) 
            throws AuthenticationException {
        
        // Validate inputs
        if (name == null || email == null || password == null) {
            throw new AuthenticationException("All fields are required");
        }
        
        // Check if email already exists
        if (database.getPassengerByEmail(email) != null) {
            throw new AuthenticationException("Email already registered");
        }
        
        // Validate password policy (NFR-6)
        ValidationResult passwordValidation = PasswordValidator.validatePassword(password);
        if (!passwordValidation.isValid()) {
            throw new AuthenticationException(passwordValidation.getMessage());
        }
        
        // Create new passenger
        String userId = generateUserId("PASS");
        String hashedPassword = hashPassword(password);
        
        Passenger passenger = new Passenger(userId, name, email, hashedPassword, 
                                           phoneNumber, passportNumber);
        
        // Save to database
        if (database.savePassenger(passenger)) {
            return passenger;
        } else {
            throw new AuthenticationException("Failed to register passenger");
        }
    }
    
    /**
     * Login a passenger
     * UC-8: Passenger Login
     */
    public Passenger loginPassenger(String email, String password) 
            throws AuthenticationException {
        
        if (email == null || password == null) {
            throw new AuthenticationException("Email and password are required");
        }
        
        Passenger passenger = database.getPassengerByEmail(email);
        
        if (passenger == null) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        // Check if account is locked (NFR-6)
        if (passenger.isAccountLocked()) {
            throw new AuthenticationException(
                "Account is locked due to multiple failed login attempts. Please contact support.");
        }
        
        // Verify password
        String hashedPassword = hashPassword(password);
        if (!passenger.getPassword().equals(hashedPassword)) {
            // Increment failed login attempts
            passenger.incrementFailedLoginAttempts();
            database.updatePassenger(passenger);
            
            int remainingAttempts = 5 - passenger.getFailedLoginAttempts();
            if (remainingAttempts > 0) {
                throw new AuthenticationException(
                    "Invalid email or password. " + remainingAttempts + " attempts remaining.");
            } else {
                throw new AuthenticationException(
                    "Account locked due to multiple failed login attempts.");
            }
        }
        
        // Successful login
        passenger.resetFailedLoginAttempts();
        passenger.setLastLogin(LocalDateTime.now());
        database.updatePassenger(passenger);
        
        return passenger;
    }
    
    /**
     * Login an admin
     */
    public Admin loginAdmin(String email, String password) throws AuthenticationException {
        if (email == null || password == null) {
            throw new AuthenticationException("Email and password are required");
        }
        
        Admin admin = database.getAdminByEmail(email);
        
        if (admin == null) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        if (admin.isAccountLocked()) {
            throw new AuthenticationException("Account is locked. Please contact system administrator.");
        }
        
        String hashedPassword = hashPassword(password);
        if (!admin.getPassword().equals(hashedPassword)) {
            admin.incrementFailedLoginAttempts();
            database.saveAdmin(admin);
            throw new AuthenticationException("Invalid email or password");
        }
        
        admin.resetFailedLoginAttempts();
        admin.setLastLogin(LocalDateTime.now());
        database.saveAdmin(admin);
        
        return admin;
    }
    
    /**
     * Login airline staff
     */
    public AirlineStaff loginAirlineStaff(String email, String password) 
            throws AuthenticationException {
        
        if (email == null || password == null) {
            throw new AuthenticationException("Email and password are required");
        }
        
        AirlineStaff staff = database.getAirlineStaffByEmail(email);
        
        if (staff == null) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        if (staff.isAccountLocked()) {
            throw new AuthenticationException("Account is locked. Please contact administrator.");
        }
        
        String hashedPassword = hashPassword(password);
        if (!staff.getPassword().equals(hashedPassword)) {
            staff.incrementFailedLoginAttempts();
            database.saveAirlineStaff(staff);
            throw new AuthenticationException("Invalid email or password");
        }
        
        staff.resetFailedLoginAttempts();
        staff.setLastLogin(LocalDateTime.now());
        database.saveAirlineStaff(staff);
        
        return staff;
    }
    
    /**
     * Update passenger profile
     * Supports FR-10 (User Profile Management)
     */
    public boolean updatePassengerProfile(Passenger passenger) {
        return database.updatePassenger(passenger);
    }
    
    /**
     * Change password
     */
    public boolean changePassword(UserAccount user, String oldPassword, String newPassword) 
            throws AuthenticationException {
        
        // Verify old password
        String hashedOldPassword = hashPassword(oldPassword);
        if (!user.getPassword().equals(hashedOldPassword)) {
            throw new AuthenticationException("Current password is incorrect");
        }
        
        // Validate new password
        ValidationResult validation = PasswordValidator.validatePassword(newPassword);
        if (!validation.isValid()) {
            throw new AuthenticationException(validation.getMessage());
        }
        
        // Update password
        user.setPassword(hashPassword(newPassword));
        
        if (user instanceof Passenger) {
            return database.updatePassenger((Passenger) user);
        } else if (user instanceof Admin) {
            return database.saveAdmin((Admin) user);
        } else if (user instanceof AirlineStaff) {
            return database.saveAirlineStaff((AirlineStaff) user);
        }
        
        return false;
    }

    // Helper methods

    private String generateUserId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" +
               (int)(Math.random() * 1000);
    }
    
    /**
     * Hash password using SHA-256
     * In production, use bcrypt or similar
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}

