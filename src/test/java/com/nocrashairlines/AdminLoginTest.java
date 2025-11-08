package com.nocrashairlines;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.model.Admin;
import com.nocrashairlines.service.AuthenticationService;

/**
 * Test to verify admin login works correctly
 */
public class AdminLoginTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Admin Login Test");
        System.out.println("========================================\n");
        
        AuthenticationService authService = new AuthenticationService();
        
        try {
            System.out.println("Testing admin login with credentials:");
            System.out.println("Email: admin@nocrashairlines.com");
            System.out.println("Password: Admin@123\n");
            
            Admin admin = authService.loginAdmin("admin@nocrashairlines.com", "Admin@123");
            
            System.out.println("✅ ADMIN LOGIN SUCCESSFUL!");
            System.out.println("\nAdmin Details:");
            System.out.println("  User ID: " + admin.getUserId());
            System.out.println("  Name: " + admin.getName());
            System.out.println("  Email: " + admin.getEmail());
            System.out.println("  Admin Level: " + admin.getAdminLevel());
            System.out.println("\n========================================");
            System.out.println("  TEST PASSED!");
            System.out.println("========================================");
            
        } catch (AuthenticationException e) {
            System.err.println("❌ ADMIN LOGIN FAILED!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\n========================================");
            System.err.println("  TEST FAILED!");
            System.err.println("========================================");
            e.printStackTrace();
        }
    }
}

