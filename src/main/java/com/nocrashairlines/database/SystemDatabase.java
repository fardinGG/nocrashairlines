package com.nocrashairlines.database;

import com.nocrashairlines.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * FR-21 - FR-25
 */
public class SystemDatabase {
    private static SystemDatabase instance;
    
    // storage
    private final Map<String, Passenger> passengers;
    private final Map<String, Admin> admins;
    private final Map<String, AirlineStaff> airlineStaff;
    private final Map<String, Flight> flights;
    private final Map<String, Booking> bookings;
    private final Map<String, Payment> payments;
    private final List<TransactionLog> transactionLogs;
    
    // Backup storage for NFR-4
    private Map<String, Object> backupData;
    private LocalDateTime lastBackupTime;

    private SystemDatabase() {
        this.passengers = new ConcurrentHashMap<>();
        this.admins = new ConcurrentHashMap<>();
        this.airlineStaff = new ConcurrentHashMap<>();
        this.flights = new ConcurrentHashMap<>();
        this.bookings = new ConcurrentHashMap<>();
        this.payments = new ConcurrentHashMap<>();
        this.transactionLogs = Collections.synchronizedList(new ArrayList<>());
        initializeDefaultData();
    }

    public static synchronized SystemDatabase getInstance() {
        if (instance == null) {
            instance = new SystemDatabase();
        }
        return instance;
    }

    // Initialize with some default data
    private void initializeDefaultData() {
        // Create default admin with hashed password
        // Password: Admin@123
        // Hashed using SHA-256
        String hashedPassword = hashPassword("Admin@123");
        Admin defaultAdmin = new Admin("ADMIN001", "System Admin", "admin@nocrashairlines.com",
                hashedPassword, "+1234567890", "SUPER_ADMIN");
        admins.put(defaultAdmin.getUserId(), defaultAdmin);
    }

    /**
     * Hash password using SHA-256 (same as AuthenticationService)
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    // Passenger Operations 
    
    public boolean savePassenger(Passenger passenger) {
        if (passenger == null || passenger.getUserId() == null) {
            return false;
        }
        passengers.put(passenger.getUserId(), passenger);
        logTransaction("SAVE_PASSENGER", passenger.getUserId(), "Passenger saved: " + passenger.getEmail());
        return true;
    }

    public Passenger getPassengerById(String passengerId) {
        return passengers.get(passengerId);
    }

    public Passenger getPassengerByEmail(String email) {
        return passengers.values().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public boolean updatePassenger(Passenger passenger) {
        if (passenger == null || !passengers.containsKey(passenger.getUserId())) {
            return false;
        }
        passengers.put(passenger.getUserId(), passenger);
        logTransaction("UPDATE_PASSENGER", passenger.getUserId(), "Passenger updated");
        return true;
    }

    public boolean deletePassenger(String passengerId) {
        Passenger removed = passengers.remove(passengerId);
        if (removed != null) {
            logTransaction("DELETE_PASSENGER", passengerId, "Passenger deleted");
            return true;
        }
        return false;
    }

    public List<Passenger> getAllPassengers() {
        return new ArrayList<>(passengers.values());
    }

    // Admin Operations 
    
    public boolean saveAdmin(Admin admin) {
        if (admin == null || admin.getUserId() == null) {
            return false;
        }
        admins.put(admin.getUserId(), admin);
        logTransaction("SAVE_ADMIN", admin.getUserId(), "Admin saved");
        return true;
    }

    public Admin getAdminById(String adminId) {
        return admins.get(adminId);
    }

    public Admin getAdminByEmail(String email) {
        return admins.values().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Airline Staff Operations 
    
    public boolean saveAirlineStaff(AirlineStaff staff) {
        if (staff == null || staff.getUserId() == null) {
            return false;
        }
        airlineStaff.put(staff.getUserId(), staff);
        logTransaction("SAVE_STAFF", staff.getUserId(), "Airline staff saved");
        return true;
    }

    public AirlineStaff getAirlineStaffById(String staffId) {
        return airlineStaff.get(staffId);
    }

    public AirlineStaff getAirlineStaffByEmail(String email) {
        return airlineStaff.values().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Flight Operations 
    
    public boolean saveFlight(Flight flight) {
        if (flight == null || flight.getFlightId() == null) {
            return false;
        }
        flights.put(flight.getFlightId(), flight);
        logTransaction("SAVE_FLIGHT", flight.getFlightId(), "Flight saved: " + flight.getFlightNumber());
        return true;
    }

    public Flight getFlightById(String flightId) {
        return flights.get(flightId);
    }

    public Flight getFlightByNumber(String flightNumber) {
        return flights.values().stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElse(null);
    }

    public boolean updateFlight(Flight flight) {
        if (flight == null || !flights.containsKey(flight.getFlightId())) {
            return false;
        }
        flights.put(flight.getFlightId(), flight);
        logTransaction("UPDATE_FLIGHT", flight.getFlightId(), "Flight updated");
        return true;
    }

    public boolean deleteFlight(String flightId) {
        Flight removed = flights.remove(flightId);
        if (removed != null) {
            logTransaction("DELETE_FLIGHT", flightId, "Flight deleted");
            return true;
        }
        return false;
    }

    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        return flights.values().stream()
                .filter(f -> f.getOrigin().equalsIgnoreCase(origin))
                .filter(f -> f.getDestination().equalsIgnoreCase(destination))
                .filter(f -> f.getDepartureTime().toLocalDate().equals(date.toLocalDate()))
                .filter(f -> "SCHEDULED".equals(f.getStatus()))
                .filter(Flight::hasAvailableSeats)
                .collect(Collectors.toList());
    }

    //  Booking Operations
    
    public boolean saveBooking(Booking booking) {
        if (booking == null || booking.getBookingId() == null) {
            return false;
        }
        bookings.put(booking.getBookingId(), booking);
        logTransaction("SAVE_BOOKING", booking.getBookingId(), "Booking saved for passenger: " + booking.getPassengerId());
        return true;
    }

    public Booking getBookingById(String bookingId) {
        return bookings.get(bookingId);
    }

    public boolean updateBooking(Booking booking) {
        if (booking == null || !bookings.containsKey(booking.getBookingId())) {
            return false;
        }
        bookings.put(booking.getBookingId(), booking);
        logTransaction("UPDATE_BOOKING", booking.getBookingId(), "Booking updated: " + booking.getStatus());
        return true;
    }

    public List<Booking> getBookingsByPassengerId(String passengerId) {
        return bookings.values().stream()
                .filter(b -> b.getPassengerId().equals(passengerId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByFlightId(String flightId) {
        return bookings.values().stream()
                .filter(b -> b.getFlightId().equals(flightId))
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    // Payment Operations 
    
    public boolean savePayment(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) {
            return false;
        }
        payments.put(payment.getPaymentId(), payment);
        logTransaction("SAVE_PAYMENT", payment.getPaymentId(), "Payment saved: " + payment.getStatus());
        return true;
    }

    public Payment getPaymentById(String paymentId) {
        return payments.get(paymentId);
    }

    public Payment getPaymentByBookingId(String bookingId) {
        return payments.values().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .findFirst()
                .orElse(null);
    }

    public boolean updatePayment(Payment payment) {
        if (payment == null || !payments.containsKey(payment.getPaymentId())) {
            return false;
        }
        payments.put(payment.getPaymentId(), payment);
        logTransaction("UPDATE_PAYMENT", payment.getPaymentId(), "Payment updated: " + payment.getStatus());
        return true;
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    // Transaction Log Operations (FR-23)
    
    private void logTransaction(String type, String entityId, String description) {
        TransactionLog log = new TransactionLog(type, entityId, description);
        transactionLogs.add(log);
    }

    public List<TransactionLog> getTransactionLogs() {
        return new ArrayList<>(transactionLogs);
    }

    public List<TransactionLog> getTransactionLogsByType(String type) {
        return transactionLogs.stream()
                .filter(log -> log.getType().equals(type))
                .collect(Collectors.toList());
    }

    // Backup & Recovery Operations (FR-24, NFR-4) 
    
    public void performBackup() {
        backupData = new HashMap<>();
        backupData.put("passengers", new HashMap<>(passengers));
        backupData.put("admins", new HashMap<>(admins));
        backupData.put("airlineStaff", new HashMap<>(airlineStaff));
        backupData.put("flights", new HashMap<>(flights));
        backupData.put("bookings", new HashMap<>(bookings));
        backupData.put("payments", new HashMap<>(payments));
        backupData.put("transactionLogs", new ArrayList<>(transactionLogs));
        lastBackupTime = LocalDateTime.now();
        logTransaction("BACKUP", "SYSTEM", "Database backup completed");
    }

    @SuppressWarnings("unchecked")
    public boolean restoreFromBackup() {
        if (backupData == null) {
            return false;
        }
        try {
            passengers.clear();
            passengers.putAll((Map<String, Passenger>) backupData.get("passengers"));
            
            admins.clear();
            admins.putAll((Map<String, Admin>) backupData.get("admins"));
            
            airlineStaff.clear();
            airlineStaff.putAll((Map<String, AirlineStaff>) backupData.get("airlineStaff"));
            
            flights.clear();
            flights.putAll((Map<String, Flight>) backupData.get("flights"));
            
            bookings.clear();
            bookings.putAll((Map<String, Booking>) backupData.get("bookings"));
            
            payments.clear();
            payments.putAll((Map<String, Payment>) backupData.get("payments"));
            
            logTransaction("RESTORE", "SYSTEM", "Database restored from backup");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDateTime getLastBackupTime() {
        return lastBackupTime;
    }

    // Statistics for Reports (FR-15)
    
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPassengers", passengers.size());
        stats.put("totalFlights", flights.size());
        stats.put("totalBookings", bookings.size());
        stats.put("confirmedBookings", bookings.values().stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus())).count());
        stats.put("cancelledBookings", bookings.values().stream()
                .filter(b -> "CANCELLED".equals(b.getStatus())).count());
        stats.put("totalRevenue", payments.values().stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount).sum());
        return stats;
    }

    // Inner class for transaction logging
    public static class TransactionLog {
        private final String logId;
        private final String type;
        private final String entityId;
        private final String description;
        private final LocalDateTime timestamp;

        public TransactionLog(String type, String entityId, String description) {
            this.logId = UUID.randomUUID().toString();
            this.type = type;
            this.entityId = entityId;
            this.description = description;
            this.timestamp = LocalDateTime.now();
        }

        public String getLogId() { return logId; }
        public String getType() { return type; }
        public String getEntityId() { return entityId; }
        public String getDescription() { return description; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s: %s", timestamp, type, entityId, description);
        }
    }
}

