package com.nocrashairlines.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.nocrashairlines.model.Passenger;
import com.nocrashairlines.service.*;

/**
 * Modern JavaFX GUI for NoCrash Airlines
 */
public class NoCrashAirlinesGUI extends Application {
    
    private Stage primaryStage;
    private Passenger currentPassenger;
    
    // Services
    private final AuthenticationService authService = new AuthenticationService();
    private final FlightService flightService = new FlightService();
    private final BookingService bookingService = new BookingService();
    private final PaymentService paymentService = new PaymentService();
    private final AdminService adminService = new AdminService();
    
    // Screens
    private WelcomeScreen welcomeScreen;
    private LoginScreen loginScreen;
    private RegisterScreen registerScreen;
    private PassengerDashboard passengerDashboard;
    private AdminDashboard adminDashboard;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("NoCrash Airlines - Flight Booking System");
        
        // Initialize screens
        welcomeScreen = new WelcomeScreen(this);
        loginScreen = new LoginScreen(this);
        registerScreen = new RegisterScreen(this);
        passengerDashboard = new PassengerDashboard(this);
        adminDashboard = new AdminDashboard(this);
        
        // Load custom CSS
        String css = getClass().getResource("/styles.css") != null ? 
            getClass().getResource("/styles.css").toExternalForm() : null;
        
        // Show welcome screen
        showWelcomeScreen();
        
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }
    
    public void showWelcomeScreen() {
        System.out.println("=== showWelcomeScreen() called ===");
        Scene scene = new Scene(welcomeScreen.getView(), 1200, 800);
        loadStylesheet(scene);
        primaryStage.setScene(scene);
        System.out.println("Welcome screen set successfully");
    }

    public void showLoginScreen(boolean isAdmin) {
        System.out.println("=== showLoginScreen() called, isAdmin=" + isAdmin + " ===");
        loginScreen.setAdminMode(isAdmin);
        Scene scene = new Scene(loginScreen.getView(), 1200, 800);
        loadStylesheet(scene);
        primaryStage.setScene(scene);
        System.out.println("Login screen set successfully");
    }

    public void showRegisterScreen() {
        Scene scene = new Scene(registerScreen.getView(), 1200, 800);
        loadStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public void showPassengerDashboard() {
        passengerDashboard.refresh();
        Scene scene = new Scene(passengerDashboard.getView(), 1200, 800);
        loadStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public void showAdminDashboard() {
        adminDashboard.refresh();
        Scene scene = new Scene(adminDashboard.getView(), 1200, 800);
        loadStylesheet(scene);
        primaryStage.setScene(scene);
    }

    private void loadStylesheet(Scene scene) {
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("Warning: Could not load stylesheet. Using inline styles.");
        }
    }
    
    // Getters
    public Passenger getCurrentPassenger() { return currentPassenger; }
    public void setCurrentPassenger(Passenger passenger) { this.currentPassenger = passenger; }
    
    public AuthenticationService getAuthService() { return authService; }
    public FlightService getFlightService() { return flightService; }
    public BookingService getBookingService() { return bookingService; }
    public PaymentService getPaymentService() { return paymentService; }
    public AdminService getAdminService() { return adminService; }
    
    public static void main(String[] args) {
        launch(args);
    }
}

