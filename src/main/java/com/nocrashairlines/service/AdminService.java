package com.nocrashairlines.service;

import com.nocrashairlines.database.SystemDatabase;
import com.nocrashairlines.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for administrative operations.
 * Supports UC-9 (Admin Manage Flights) and UC-10 (Generate Reports)
 * Supports FR-12, FR-13, FR-14, FR-15
 */
public class AdminService {
    
    private final SystemDatabase database;
    private final FlightService flightService;
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public AdminService() {
        this.database = SystemDatabase.getInstance();
        this.flightService = new FlightService();
    }
    
    // ==================== Flight Management (UC-9) ====================
    
    /**
     * Add a new flight
     * FR-12: Flight Management
     */
    public Flight addFlight(String flightNumber, String origin, String destination,
                           LocalDateTime departureTime, LocalDateTime arrivalTime,
                           String aircraftType, int totalSeats, String gate) {
        
        Flight flight = flightService.addFlight(flightNumber, origin, destination,
                                               departureTime, arrivalTime, aircraftType, totalSeats);
        flight.setGate(gate);
        
        // Set default prices for different classes
        flight.setClassPrice("ECONOMY", 200.0);
        flight.setClassPrice("BUSINESS", 500.0);
        flight.setClassPrice("FIRST_CLASS", 1000.0);
        
        flightService.updateFlight(flight);
        return flight;
    }
    
    /**
     * Update flight details
     * FR-12: Flight Management
     */
    public boolean updateFlight(Flight flight) {
        return flightService.updateFlight(flight);
    }
    
    /**
     * Delete a flight
     * FR-12: Flight Management
     */
    public boolean deleteFlight(String flightId) {
        return flightService.deleteFlight(flightId);
    }
    
    /**
     * Update seat availability
     * FR-13: Seat Inventory Management
     */
    public boolean updateSeatInventory(String flightId, int availableSeats) {
        return flightService.updateSeatAvailability(flightId, availableSeats);
    }
    
    /**
     * Monitor all bookings
     * FR-14: Booking Oversight
     */
    public List<Booking> monitorAllBookings() {
        return database.getAllBookings();
    }
    
    /**
     * Get bookings by status
     * FR-14: Booking Oversight
     */
    public List<Booking> getBookingsByStatus(String status) {
        return database.getAllBookings().stream()
                .filter(b -> b.getStatus().equals(status))
                .collect(Collectors.toList());
    }
    
    /**
     * Manually update a booking
     * FR-14: Booking Oversight
     */
    public boolean manuallyUpdateBooking(Booking booking) {
        return database.updateBooking(booking);
    }
    
    // ==================== Report Generation (UC-10) ====================
    
    /**
     * Generate daily sales report
     * FR-15: Report Generation and Analytics
     */
    public String generateDailySalesReport(LocalDateTime date) {
        List<Payment> payments = database.getAllPayments().stream()
                .filter(p -> p.getPaymentDate().toLocalDate().equals(date.toLocalDate()))
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .collect(Collectors.toList());
        
        double totalRevenue = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("     DAILY SALES REPORT\n");
        report.append("     Date: ").append(date.toLocalDate()).append("\n");
        report.append("========================================\n\n");
        report.append("Total Transactions: ").append(payments.size()).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n\n");
        
        report.append("Payment Method Breakdown:\n");
        Map<String, Long> methodCount = payments.stream()
                .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.counting()));
        methodCount.forEach((method, count) -> 
            report.append("  ").append(method).append(": ").append(count).append("\n"));
        
        report.append("\n========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generate passenger trends report
     * FR-15: Report Generation and Analytics
     */
    public String generatePassengerTrendsReport() {
        List<Passenger> passengers = database.getAllPassengers();
        List<Booking> bookings = database.getAllBookings();
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("     PASSENGER TRENDS REPORT\n");
        report.append("========================================\n\n");
        report.append("Total Registered Passengers: ").append(passengers.size()).append("\n");
        report.append("Total Bookings: ").append(bookings.size()).append("\n\n");
        
        // Booking status breakdown
        report.append("Booking Status Breakdown:\n");
        Map<String, Long> statusCount = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));
        statusCount.forEach((status, count) -> 
            report.append("  ").append(status).append(": ").append(count).append("\n"));
        
        // Travel class preferences
        report.append("\nTravel Class Preferences:\n");
        Map<String, Long> classCount = bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .collect(Collectors.groupingBy(Booking::getTravelClass, Collectors.counting()));
        classCount.forEach((travelClass, count) -> 
            report.append("  ").append(travelClass).append(": ").append(count).append("\n"));
        
        report.append("\n========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generate most booked routes report
     * FR-15: Report Generation and Analytics
     */
    public String generateMostBookedRoutesReport() {
        List<Booking> confirmedBookings = database.getAllBookings().stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .collect(Collectors.toList());
        
        Map<String, Integer> routeCount = new HashMap<>();
        
        for (Booking booking : confirmedBookings) {
            Flight flight = database.getFlightById(booking.getFlightId());
            if (flight != null) {
                String route = flight.getOrigin() + " → " + flight.getDestination();
                routeCount.put(route, routeCount.getOrDefault(route, 0) + 1);
            }
        }
        
        // Sort by count descending
        List<Map.Entry<String, Integer>> sortedRoutes = routeCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("     MOST BOOKED ROUTES REPORT\n");
        report.append("========================================\n\n");
        
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedRoutes) {
            report.append(rank++).append(". ").append(entry.getKey())
                  .append(" - ").append(entry.getValue()).append(" bookings\n");
        }
        
        report.append("\n========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generate comprehensive system statistics report
     * FR-15: Report Generation and Analytics
     */
    public String generateSystemStatisticsReport() {
        Map<String, Object> stats = database.getSystemStatistics();
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("     SYSTEM STATISTICS REPORT\n");
        report.append("     Generated: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        report.append("========================================\n\n");
        
        report.append("USERS:\n");
        report.append("  Total Passengers: ").append(stats.get("totalPassengers")).append("\n\n");
        
        report.append("FLIGHTS:\n");
        report.append("  Total Flights: ").append(stats.get("totalFlights")).append("\n\n");
        
        report.append("BOOKINGS:\n");
        report.append("  Total Bookings: ").append(stats.get("totalBookings")).append("\n");
        report.append("  Confirmed Bookings: ").append(stats.get("confirmedBookings")).append("\n");
        report.append("  Cancelled Bookings: ").append(stats.get("cancelledBookings")).append("\n\n");
        
        report.append("REVENUE:\n");
        report.append("  Total Revenue: $").append(String.format("%.2f", stats.get("totalRevenue"))).append("\n\n");
        
        report.append("========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generate flight occupancy report
     */
    public String generateFlightOccupancyReport() {
        List<Flight> flights = database.getAllFlights();
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("     FLIGHT OCCUPANCY REPORT\n");
        report.append("========================================\n\n");
        
        for (Flight flight : flights) {
            int occupiedSeats = flight.getTotalSeats() - flight.getAvailableSeats();
            double occupancyRate = (occupiedSeats * 100.0) / flight.getTotalSeats();
            
            report.append("Flight: ").append(flight.getFlightNumber()).append("\n");
            report.append("  Route: ").append(flight.getOrigin()).append(" → ")
                  .append(flight.getDestination()).append("\n");
            report.append("  Departure: ").append(flight.getDepartureTime().format(DATE_FORMATTER)).append("\n");
            report.append("  Occupancy: ").append(occupiedSeats).append("/")
                  .append(flight.getTotalSeats()).append(" (")
                  .append(String.format("%.1f", occupancyRate)).append("%)\n");
            report.append("  Status: ").append(flight.getStatus()).append("\n\n");
        }
        
        report.append("========================================\n");
        
        return report.toString();
    }
    
    /**
     * Perform database backup
     * FR-24: Data Backup & Recovery
     */
    public void performDatabaseBackup() {
        database.performBackup();
    }
    
    /**
     * Restore database from backup
     * FR-24: Data Backup & Recovery
     */
    public boolean restoreDatabaseFromBackup() {
        return database.restoreFromBackup();
    }
}

