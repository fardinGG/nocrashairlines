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
        System.out.println("Available cities: Toronto, Vancouver, Montreal, Calgary");
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();

        LocalDateTime searchDate = LocalDateTime.now().plusDays(1);
        List<Flight> flights = flightService.searchFlights(origin, destination, searchDate);

        if (flights.isEmpty()) {
            System.out.println("\n❌ No flights found for " + origin + " → " + destination);
            System.out.println("Try searching for: Toronto → Vancouver, Toronto → Montreal, or Vancouver → Calgary");
        } else {
            System.out.println("\n✅ Found " + flights.size() + " flight(s):\n");
            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);
                System.out.println((i + 1) + ". Flight " + f.getFlightNumber());
                System.out.println("   Route: " + f.getOrigin() + " → " + f.getDestination());
                System.out.println("   Departure: " + f.getDepartureTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("   Arrival: " + f.getArrivalTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("   Duration: " + f.getDurationInMinutes() / 60 + " hours");
                System.out.println("   Available Seats: " + f.getAvailableSeats() + "/" + f.getTotalSeats());
                System.out.println("   Gate: " + (f.getGate() != null ? f.getGate() : "TBA"));
                System.out.println("   Prices:");
                System.out.println("     - Economy: $" + f.getClassPrice("ECONOMY"));
                System.out.println("     - Business: $" + f.getClassPrice("BUSINESS"));
                System.out.println("     - First Class: $" + f.getClassPrice("FIRST_CLASS"));
                System.out.println();
            }
            System.out.println("💡 Tip: Use option 4 (Book Ticket) to book one of these flights!");
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

            // First, let user search for flights
            System.out.print("Origin City: ");
            String origin = scanner.nextLine();
            System.out.print("Destination City: ");
            String destination = scanner.nextLine();

            LocalDateTime searchDate = LocalDateTime.now().plusDays(1);
            List<Flight> flights = flightService.searchFlights(origin, destination, searchDate);

            if (flights.isEmpty()) {
                System.out.println("\nNo flights found for " + origin + " → " + destination);
                System.out.println("Please try different cities.");
                return;
            }

            // Display available flights with numbers
            System.out.println("\n=== Available Flights ===");
            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);
                System.out.println("\n" + (i + 1) + ". Flight " + f.getFlightNumber());
                System.out.println("   Route: " + f.getOrigin() + " → " + f.getDestination());
                System.out.println("   Departure: " + f.getDepartureTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("   Arrival: " + f.getArrivalTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("   Available Seats: " + f.getAvailableSeats() + "/" + f.getTotalSeats());
                System.out.println("   Prices:");
                System.out.println("     - Economy: $" + f.getClassPrice("ECONOMY"));
                System.out.println("     - Business: $" + f.getClassPrice("BUSINESS"));
                System.out.println("     - First Class: $" + f.getClassPrice("FIRST_CLASS"));
            }

            // Let user select a flight
            System.out.print("\nSelect flight number (1-" + flights.size() + ") or 0 to cancel: ");
            int flightChoice = getIntInput();

            if (flightChoice == 0) {
                System.out.println("Booking cancelled.");
                return;
            }

            if (flightChoice < 1 || flightChoice > flights.size()) {
                System.out.println("Invalid flight selection.");
                return;
            }

            Flight selectedFlight = flights.get(flightChoice - 1);

            // Select travel class
            System.out.print("\nTravel Class (1=ECONOMY, 2=BUSINESS, 3=FIRST_CLASS): ");
            int classChoice = getIntInput();
            String travelClass;
            switch (classChoice) {
                case 1 -> travelClass = "ECONOMY";
                case 2 -> travelClass = "BUSINESS";
                case 3 -> travelClass = "FIRST_CLASS";
                default -> {
                    System.out.println("Invalid class selection. Defaulting to ECONOMY.");
                    travelClass = "ECONOMY";
                }
            }

            // Create booking
            Booking booking = bookingService.createBooking(
                currentPassenger.getUserId(), selectedFlight.getFlightId(), currentPassenger.getName(),
                currentPassenger.getEmail(), currentPassenger.getPhoneNumber(),
                currentPassenger.getPassportNumber(), travelClass
            );

            System.out.println("\n✅ Booking created successfully!");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Flight: " + selectedFlight.getFlightNumber());
            System.out.println("Seat: " + booking.getSeatNumber());
            System.out.println("Amount: $" + booking.getTotalAmount());

            // Process payment
            System.out.print("\nProceed with payment? (yes/no): ");
            String proceed = scanner.nextLine();

            if (proceed.equalsIgnoreCase("yes")) {
                System.out.println("\nPayment Methods:");
                System.out.println("1. Credit Card");
                System.out.println("2. Debit Card");
                System.out.println("3. Digital Wallet");
                System.out.print("Select payment method (1-3): ");
                int paymentChoice = getIntInput();

                String paymentMethod;
                switch (paymentChoice) {
                    case 1 -> paymentMethod = "CREDIT_CARD";
                    case 2 -> paymentMethod = "DEBIT_CARD";
                    case 3 -> paymentMethod = "DIGITAL_WALLET";
                    default -> {
                        System.out.println("Invalid selection. Using Credit Card.");
                        paymentMethod = "CREDIT_CARD";
                    }
                }

                System.out.print("Card Last 4 Digits: ");
                String cardDigits = scanner.nextLine();

                Payment payment = paymentService.processPayment(
                    booking.getBookingId(), currentPassenger.getUserId(),
                    paymentMethod, cardDigits
                );

                System.out.println("\n✅ Payment successful!");
                System.out.println("Transaction Reference: " + payment.getTransactionReference());
                System.out.println("E-ticket has been sent to your email!");
            } else {
                System.out.println("\nBooking saved but not confirmed. Please complete payment to confirm.");
            }
        } catch (BookingException | PaymentException e) {
            System.out.println("❌ Error: " + e.getMessage());
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

