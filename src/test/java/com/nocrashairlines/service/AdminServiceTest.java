package com.nocrashairlines.service;

import com.nocrashairlines.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Admin Service Tests")
class AdminServiceTest {
    
    private AdminService adminService;
    
    @BeforeEach
    void setUp() {
        adminService = new AdminService();
        System.out.println("Setting up AdminService test...");
    }
    
    @Test
    @DisplayName("Should add flight successfully")
    void testAddFlight() {
        System.out.println("Testing add flight...");
        
        LocalDateTime departureTime = LocalDateTime.now().plusDays(2).withHour(9).withMinute(0);
        LocalDateTime arrivalTime = departureTime.plusHours(3);
        
        Flight flight = adminService.addFlight(
            "ADMIN101",
            "Ottawa",
            "Halifax",
            departureTime,
            arrivalTime,
            "Boeing 737",
            180,
            "F12"
        );
        
        assertNotNull(flight, "Flight should not be null");
        assertEquals("ADMIN101", flight.getFlightNumber(), "Flight number should match");
        assertEquals("Ottawa", flight.getOrigin(), "Departure city should match");
        assertEquals("Halifax", flight.getDestination(), "Arrival city should match");
        assertEquals(180, flight.getTotalSeats(), "Total seats should match");
        assertEquals("F12", flight.getGate(), "Gate should match");
        
        System.out.println("✓ Flight added: " + flight.getFlightId());
        System.out.println("✓ Add flight test passed!");
    }
    
    @Test
    @DisplayName("Should update flight successfully")
    void testUpdateFlight() {
        System.out.println("Testing update flight...");
        
        // Add flight first
        LocalDateTime originalTime = LocalDateTime.now().plusDays(3).withHour(11).withMinute(0);
        Flight originalFlight = adminService.addFlight(
            "UPDATE101",
            "Toronto",
            "Vancouver",
            originalTime,
            originalTime.plusHours(5),
            "Airbus A320",
            150,
            "G5"
        );
        
        // Update the flight
        LocalDateTime newTime = originalTime.plusHours(2);
        originalFlight.setFlightNumber("UPDATE101");
        originalFlight.setDepartureTime(newTime);
        originalFlight.setArrivalTime(newTime.plusHours(5));
        originalFlight.setAircraftType("Boeing 777");
        originalFlight.setTotalSeats(200);
        originalFlight.setGate("H8");

        boolean updated = adminService.updateFlight(originalFlight);

        assertTrue(updated, "Flight should be updated successfully");
        System.out.println("✓ Update flight test passed!");
    }
    
    @Test
    @DisplayName("Should cancel flight successfully")
    void testCancelFlight() {
        System.out.println("Testing cancel flight...");
        
        LocalDateTime futureTime = LocalDateTime.now().plusDays(4).withHour(15).withMinute(0);
        Flight flight = adminService.addFlight(
            "CANCEL101",
            "Montreal",
            "Calgary",
            futureTime,
            futureTime.plusHours(4),
            "Boeing 737",
            180,
            "J3"
        );
        
        boolean cancelled = adminService.deleteFlight(flight.getFlightId());
        assertTrue(cancelled, "Flight should be cancelled successfully");
        
        System.out.println("✓ Cancel flight test passed!");
    }
    
    @Test
    @DisplayName("Should generate daily sales report")
    void testGenerateDailySalesReport() {
        System.out.println("Testing daily sales report generation...");
        
        LocalDateTime today = LocalDateTime.now();
        String report = adminService.generateDailySalesReport(today);
        
        assertNotNull(report, "Report should not be null");
        assertFalse(report.isEmpty(), "Report should not be empty");
        assertTrue(report.contains("Daily Sales Report"), "Report should contain title");
        
        System.out.println("✓ Daily sales report test passed!");
    }
    
    @Test
    @DisplayName("Should generate passenger trends report")
    void testGeneratePassengerTrendsReport() {
        System.out.println("Testing passenger trends report generation...");
        
        String report = adminService.generatePassengerTrendsReport();
        
        assertNotNull(report, "Report should not be null");
        assertFalse(report.isEmpty(), "Report should not be empty");
        assertTrue(report.contains("Passenger Trends Report"), "Report should contain title");
        
        System.out.println("✓ Passenger trends report test passed!");
    }
    
    @Test
    @DisplayName("Should generate system statistics report")
    void testGenerateSystemStatisticsReport() {
        System.out.println("Testing system statistics report generation...");
        
        String report = adminService.generateSystemStatisticsReport();
        
        assertNotNull(report, "Report should not be null");
        assertFalse(report.isEmpty(), "Report should not be empty");
        assertTrue(report.contains("System Statistics Report"), "Report should contain title");
        
        System.out.println("✓ System statistics report test passed!");
    }
}