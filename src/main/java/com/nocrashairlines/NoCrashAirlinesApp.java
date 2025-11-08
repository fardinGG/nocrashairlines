package com.nocrashairlines;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.exception.PaymentException;
import com.nocrashairlines.model.*;
import com.nocrashairlines.service.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for NoCrash Airlines Flight Ticket System
 * Demonstrates all 10 use cases from the requirements
 */
public class NoCrashAirlinesApp {
    
    private final AuthenticationService authService;
    private final FlightService flightService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final AdminService adminService;
    private final Scanner scanner;
    
    private Passenger currentPassenger;
    private Admin currentAdmin;
    
    public NoCrashAirlinesApp() {
        this.authService = new AuthenticationService();
        this.flightService = new FlightService();
        this.bookingService = new BookingService();
        this.paymentService = new PaymentService();
        this.adminService = new AdminService();
        this.scanner = new Scanner(System.in);
        
        initializeSampleData();
    }
    
    /**
     * Initialize sample data for demonstration
     */
    private void initializeSampleData() {
        try {
            // Add sample flights
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
            
            adminService.addFlight("NC101", "Toronto", "Vancouver", 
                tomorrow, tomorrow.plusHours(5), "Boeing 737", 180, "A12");
            
            adminService.addFlight("NC202", "Toronto", "Montreal", 
                tomorrow.plusHours(2), tomorrow.plusHours(3).plusMinutes(30), 
                "Airbus A320", 150, "B5");
            
            adminService.addFlight("NC303", "Vancouver", "Calgary", 
                tomorrow.plusHours(4), tomorrow.plusHours(5).plusMinutes(30), 
                "Boeing 737", 180, "C8");
            
            System.out.println("Sample flights initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
    
    public void start() {
        System.out.println("========================================");
        System.out.println("  Welcome to NoCrash Airlines");
        System.out.println("  Flight Ticket Booking System");
        System.out.println("========================================\n");
        
        while (true) {
            showMainMenu();
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> passengerMenu();
                case 2 -> adminMenu();
                case 3 -> {
                    System.out.println("\nThank you for using NoCrash Airlines!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Passenger Portal");
        System.out.println("2. Admin Portal");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void passengerMenu() {
        while (true) {
            System.out.println("\n=== Passenger Portal ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Search Flights");
            System.out.println("4. Book Ticket");
            System.out.println("5. View My Bookings");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Reschedule Booking");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> registerPassenger();
                case 2 -> loginPassenger();
                case 3 -> searchFlights();
                case 4 -> bookTicket();
                case 5 -> viewMyBookings();
                case 6 -> cancelBooking();
                case 7 -> rescheduleBooking();
                case 8 -> {
                    currentPassenger = null;
                    System.out.println("Logged out successfully!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void adminMenu() {
        // Login admin first
        if (currentAdmin == null) {
            try {
                System.out.print("\nAdmin Email: ");
                String email = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                
                currentAdmin = authService.loginAdmin(email, password);
                System.out.println("Admin login successful! Welcome, " + currentAdmin.getName());
            } catch (AuthenticationException e) {
                System.out.println("Login failed: " + e.getMessage());
                return;
            }
        }
        
        while (true) {
            System.out.println("\n=== Admin Portal ===");
            System.out.println("1. Add Flight");
            System.out.println("2. View All Flights");
            System.out.println("3. View All Bookings");
            System.out.println("4. Generate Daily Sales Report");
            System.out.println("5. Generate Passenger Trends Report");
            System.out.println("6. Generate Most Booked Routes Report");
            System.out.println("7. Generate System Statistics Report");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> addFlight();
                case 2 -> viewAllFlights();
                case 3 -> viewAllBookings();
                case 4 -> System.out.println("\n" + adminService.generateDailySalesReport(LocalDateTime.now()));
                case 5 -> System.out.println("\n" + adminService.generatePassengerTrendsReport());
                case 6 -> System.out.println("\n" + adminService.generateMostBookedRoutesReport());
                case 7 -> System.out.println("\n" + adminService.generateSystemStatisticsReport());
                case 8 -> {
                    currentAdmin = null;
                    System.out.println("Admin logged out successfully!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    // UC-7: Passenger Registration
    private void registerPassenger() {
        try {
            System.out.println("\n=== Passenger Registration ===");
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password (min 8 chars, 1 number, 1 special char): ");
            String password = scanner.nextLine();
            System.out.print("Phone Number: ");
            String phone = scanner.nextLine();
            System.out.print("Passport Number: ");
            String passport = scanner.nextLine();
            
            Passenger passenger = authService.registerPassenger(name, email, password, phone, passport);
            System.out.println("\nRegistration successful! Your User ID: " + passenger.getUserId());
        } catch (AuthenticationException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
    
    // UC-8: Passenger Login
    private void loginPassenger() {
        try {
            System.out.println("\n=== Passenger Login ===");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            currentPassenger = authService.loginPassenger(email, password);
            System.out.println("Login successful! Welcome, " + currentPassenger.getName());
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
    
    // UC-1: Search Flights
    private void searchFlights() {
        System.out.println("\n=== Search Flights ===");
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();
        
        LocalDateTime searchDate = LocalDateTime.now().plusDays(1);
        List<Flight> flights = flightService.searchFlights(origin, destination, searchDate);
        
        if (flights.isEmpty()) {
            System.out.println("No flights found for the given criteria.");
        } else {
            System.out.println("\nAvailable Flights:");
            for (Flight flight : flights) {
                System.out.println(flight);
            }
        }
    }
    
    // UC-2 & UC-3: Book Ticket and Make Payment
    private void bookTicket() {
        if (currentPassenger == null) {
            System.out.println("Please login first!");
            return;
        }
        
        try {
            System.out.println("\n=== Book Ticket ===");
            System.out.print("Flight ID: ");
            String flightId = scanner.nextLine();
            System.out.print("Travel Class (ECONOMY/BUSINESS/FIRST_CLASS): ");
            String travelClass = scanner.nextLine();
            
            Booking booking = bookingService.createBooking(
                currentPassenger.getUserId(), flightId, currentPassenger.getName(),
                currentPassenger.getEmail(), currentPassenger.getPhoneNumber(),
                currentPassenger.getPassportNumber(), travelClass
            );
            
            System.out.println("\nBooking created successfully!");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Amount: $" + booking.getTotalAmount());
            
            // Process payment
            System.out.print("\nProceed with payment? (yes/no): ");
            String proceed = scanner.nextLine();
            
            if (proceed.equalsIgnoreCase("yes")) {
                System.out.print("Payment Method (CREDIT_CARD/DEBIT_CARD/DIGITAL_WALLET): ");
                String paymentMethod = scanner.nextLine();
                System.out.print("Card Last 4 Digits: ");
                String cardDigits = scanner.nextLine();
                
                Payment payment = paymentService.processPayment(
                    booking.getBookingId(), currentPassenger.getUserId(),
                    paymentMethod, cardDigits
                );
                
                System.out.println("\nPayment successful!");
                System.out.println("Transaction Reference: " + payment.getTransactionReference());
                System.out.println("E-ticket has been sent to your email!");
            }
        } catch (BookingException | PaymentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // UC-6: View Booking
    private void viewMyBookings() {
        if (currentPassenger == null) {
            System.out.println("Please login first!");
            return;
        }
        
        List<Booking> bookings = bookingService.getBookingsByPassengerId(currentPassenger.getUserId());
        
        if (bookings.isEmpty()) {
            System.out.println("\nYou have no bookings.");
        } else {
            System.out.println("\n=== Your Bookings ===");
            for (Booking booking : bookings) {
                System.out.println(booking);
            }
        }
    }
    
    // UC-4: Cancel Booking
    private void cancelBooking() {
        if (currentPassenger == null) {
            System.out.println("Please login first!");
            return;
        }
        
        try {
            System.out.print("\nEnter Booking ID to cancel: ");
            String bookingId = scanner.nextLine();
            
            bookingService.cancelBooking(bookingId);
            System.out.println("Booking cancelled successfully!");
            
            // Process refund
            System.out.print("Process refund? (yes/no): ");
            String proceed = scanner.nextLine();
            if (proceed.equalsIgnoreCase("yes")) {
                paymentService.processRefund(bookingId, "Customer requested cancellation");
                System.out.println("Refund processed successfully!");
            }
        } catch (BookingException | PaymentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // UC-5: Reschedule Flight
    private void rescheduleBooking() {
        if (currentPassenger == null) {
            System.out.println("Please login first!");
            return;
        }
        
        try {
            System.out.print("\nEnter Booking ID to reschedule: ");
            String bookingId = scanner.nextLine();
            System.out.print("Enter New Flight ID: ");
            String newFlightId = scanner.nextLine();
            
            bookingService.rescheduleBooking(bookingId, newFlightId);
            System.out.println("Booking rescheduled successfully!");
        } catch (BookingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // UC-9: Admin Manage Flights
    private void addFlight() {
        System.out.println("\n=== Add New Flight ===");
        System.out.print("Flight Number: ");
        String flightNumber = scanner.nextLine();
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();
        System.out.print("Total Seats: ");
        int totalSeats = getIntInput();
        System.out.print("Gate: ");
        String gate = scanner.nextLine();
        
        LocalDateTime departure = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime arrival = departure.plusHours(3);
        
        Flight flight = adminService.addFlight(flightNumber, origin, destination,
            departure, arrival, "Boeing 737", totalSeats, gate);
        
        System.out.println("\nFlight added successfully!");
        System.out.println(flight);
    }
    
    private void viewAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        System.out.println("\n=== All Flights ===");
        for (Flight flight : flights) {
            System.out.println(flight);
        }
    }
    
    private void viewAllBookings() {
        List<Booking> bookings = adminService.monitorAllBookings();
        System.out.println("\n=== All Bookings ===");
        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    public static void main(String[] args) {
        NoCrashAirlinesApp app = new NoCrashAirlinesApp();
        app.start();
    }
}

