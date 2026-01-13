package com.nocrashairlines.service;

import com.nocrashairlines.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flight Service Tests")
class FlightServiceTest {

    private FlightService flightService;
    private AdminService adminService;
    private static long testCounter = 0;

    @BeforeEach
    void setUp() {
        flightService = new FlightService();
        adminService = new AdminService();
        System.out.println("Setting up FlightService test...");

        // Add some test flights with unique flight numbers using timestamp and counter
        long timestamp = System.currentTimeMillis();
        long uniqueId1 = timestamp + (testCounter++);
        long uniqueId2 = timestamp + (testCounter++);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        adminService.addFlight("TEST" + uniqueId1, "Toronto", "Vancouver",
                              tomorrow, tomorrow.plusHours(5), "Boeing 737", 180, "A12");
        adminService.addFlight("TEST" + uniqueId2, "Montreal", "Calgary",
                              tomorrow.plusHours(2), tomorrow.plusHours(7), "Airbus A320", 150, "B5");
    }
    
    @Test
    @DisplayName("Should get all flights")
    void testGetAllFlights() {
        System.out.println("Testing get all flights...");
        List<Flight> flights = flightService.getAllFlights();
        
        assertNotNull(flights, "Flights list should not be null");
        assertTrue(flights.size() >= 2, "Should have at least 2 test flights");
        System.out.println("✓ Found " + flights.size() + " flights");
        System.out.println("✓ Get all flights test passed!");
    }
    
    @Test
    @DisplayName("Should search flights by route")
    void testSearchFlights() {
        System.out.println("Testing search flights...");
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Flight> flights = flightService.searchFlights("Toronto", "Vancouver", tomorrow);

        assertNotNull(flights, "Search results should not be null");
        assertFalse(flights.isEmpty(), "Should find Toronto to Vancouver flight");
        assertEquals("Toronto", flights.get(0).getOrigin(), "Origin should be Toronto");
        assertEquals("Vancouver", flights.get(0).getDestination(), "Destination should be Vancouver");
        System.out.println("✓ Search flights test passed!");
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent route")
    void testSearchNonExistentRoute() {
        System.out.println("Testing search for non-existent route...");
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Flight> flights = flightService.searchFlights("Toronto", "London", tomorrow);
        
        assertNotNull(flights, "Search results should not be null");
        assertTrue(flights.isEmpty(), "Should return empty list for non-existent route");
        System.out.println("✓ Non-existent route test passed!");
    }
    
    @Test
    @DisplayName("Should get flight by valid ID")
    void testGetFlightById() {
        System.out.println("Testing get flight by ID...");
        List<Flight> allFlights = flightService.getAllFlights();
        assertFalse(allFlights.isEmpty(), "Should have flights to test with");
        
        String flightId = allFlights.get(0).getFlightId();
        Flight flight = flightService.getFlightById(flightId);
        
        assertNotNull(flight, "Flight should be found");
        assertEquals(flightId, flight.getFlightId(), "Flight ID should match");
        System.out.println("✓ Get flight by ID test passed!");
    }
    
    @Test
    @DisplayName("Should return null for invalid flight ID")
    void testGetFlightByInvalidId() {
        System.out.println("Testing get flight by invalid ID...");
        Flight flight = flightService.getFlightById("INVALID_ID");
        
        assertNull(flight, "Should return null for invalid ID");
        System.out.println("✓ Invalid flight ID test passed!");
    }
}