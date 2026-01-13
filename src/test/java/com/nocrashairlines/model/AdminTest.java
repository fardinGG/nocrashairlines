package com.nocrashairlines.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Admin model
 * Tests FR-11 (Admin Authentication and Management)
 */
@DisplayName("Admin Model Tests")
class AdminTest {
    
    private Admin admin;
    
    @BeforeEach
    void setUp() {
        admin = new Admin(
            "ADMIN-001",
            "Admin User",
            "admin@nocrashairlines.com",
            "hashedAdminPassword",
            "+1234567890",
            "SUPER_ADMIN"
        );
    }
    
    @Test
    @DisplayName("Should create admin with valid data")
    void testAdminCreation() {
        assertNotNull(admin, "Admin should not be null");
        assertEquals("ADMIN-001", admin.getUserId(), "User ID should match");
        assertEquals("Admin User", admin.getName(), "Name should match");
        assertEquals("admin@nocrashairlines.com", admin.getEmail(), "Email should match");
        assertEquals("+1234567890", admin.getPhoneNumber(), "Phone should match");
        assertEquals("SUPER_ADMIN", admin.getAdminLevel(), "Admin level should match");
    }
    
    @Test
    @DisplayName("Should set and get admin level")
    void testAdminLevel() {
        assertEquals("SUPER_ADMIN", admin.getAdminLevel(), "Initial level should be SUPER_ADMIN");
        
        admin.setAdminLevel("ADMIN");
        assertEquals("ADMIN", admin.getAdminLevel(), "Admin level should be ADMIN");
        
        admin.setAdminLevel("MODERATOR");
        assertEquals("MODERATOR", admin.getAdminLevel(), "Admin level should be MODERATOR");
    }
    
    @Test
    @DisplayName("Should set and get department")
    void testDepartment() {
        assertNull(admin.getDepartment(), "Department should be null initially");
        
        admin.setDepartment("Operations");
        assertEquals("Operations", admin.getDepartment(), "Department should be Operations");
        
        admin.setDepartment("Customer Service");
        assertEquals("Customer Service", admin.getDepartment(), "Department should be Customer Service");
    }
    
    @Test
    @DisplayName("Should update admin profile information")
    void testUpdateAdminProfile() {
        admin.setName("Senior Admin");
        admin.setEmail("senior.admin@nocrashairlines.com");
        admin.setPhoneNumber("+9876543210");
        admin.setDepartment("Management");
        
        assertEquals("Senior Admin", admin.getName(), "Name should be updated");
        assertEquals("senior.admin@nocrashairlines.com", admin.getEmail(), "Email should be updated");
        assertEquals("+9876543210", admin.getPhoneNumber(), "Phone should be updated");
        assertEquals("Management", admin.getDepartment(), "Department should be updated");
    }
    
    @Test
    @DisplayName("Should inherit UserAccount properties")
    void testUserAccountInheritance() {
        assertNotNull(admin.getUserId(), "Should have user ID from UserAccount");
        assertNotNull(admin.getName(), "Should have name from UserAccount");
        assertNotNull(admin.getEmail(), "Should have email from UserAccount");
        assertNotNull(admin.getPassword(), "Should have password from UserAccount");
        assertNotNull(admin.getPhoneNumber(), "Should have phone from UserAccount");
        assertNotNull(admin.getCreatedAt(), "Should have creation date from UserAccount");
    }
    
    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String result = admin.toString();
        
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("ADMIN-001"), "Should contain user ID");
        assertTrue(result.contains("Admin User"), "Should contain name");
        assertTrue(result.contains("admin@nocrashairlines.com"), "Should contain email");
        assertTrue(result.contains("SUPER_ADMIN"), "Should contain admin level");
    }
    
    @Test
    @DisplayName("Should support different admin levels")
    void testDifferentAdminLevels() {
        Admin superAdmin = new Admin("ADMIN-001", "Super Admin", "super@nc.com", "pass", "+111", "SUPER_ADMIN");
        Admin regularAdmin = new Admin("ADMIN-002", "Regular Admin", "admin@nc.com", "pass", "+222", "ADMIN");
        Admin moderator = new Admin("ADMIN-003", "Moderator", "mod@nc.com", "pass", "+333", "MODERATOR");
        
        assertEquals("SUPER_ADMIN", superAdmin.getAdminLevel(), "Should be SUPER_ADMIN");
        assertEquals("ADMIN", regularAdmin.getAdminLevel(), "Should be ADMIN");
        assertEquals("MODERATOR", moderator.getAdminLevel(), "Should be MODERATOR");
    }
}

