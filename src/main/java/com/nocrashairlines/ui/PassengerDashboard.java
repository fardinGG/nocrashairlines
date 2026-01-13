package com.nocrashairlines.ui;

import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Flight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PassengerDashboard {
    
    private final NoCrashAirlinesGUI app;
    private BorderPane view;
    private VBox contentArea;
    private Label welcomeLabel;
    
    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    public PassengerDashboard(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }
    
    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #f8fafc;");
        
        // Top bar
        HBox topBar = createTopBar();
        view.setTop(topBar);
        
        // Sidebar
        VBox sidebar = createSidebar();
        view.setLeft(sidebar);
        
        // Content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");
        
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        
        scrollPane.setContent(contentArea);
        view.setCenter(scrollPane);
        
        // Show dashboard by default
        showDashboard();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(20, 30, 20, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label logo = new Label("✈ NoCrash Airlines");
        logo.setFont(Font.font("System", FontWeight.BOLD, 24));
        logo.setStyle("-fx-text-fill: #2563eb;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        welcomeLabel = new Label();
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        welcomeLabel.setStyle("-fx-text-fill: #1e293b;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().addAll("btn", "btn-danger");
        logoutBtn.setOnAction(e -> {
            app.setCurrentPassenger(null);
            app.showWelcomeScreen();
        });
        
        topBar.getChildren().addAll(logo, spacer, welcomeLabel, logoutBtn);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.getStyleClass().add("sidebar");
        
        Label menuLabel = new Label("MENU");
        menuLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        menuLabel.setStyle("-fx-text-fill: #94a3b8;");
        menuLabel.setPadding(new Insets(10, 0, 10, 10));
        
        Button dashboardBtn = createSidebarButton("🏠 Dashboard");
        dashboardBtn.setOnAction(e -> showDashboard());
        
        Button searchBtn = createSidebarButton("🔍 Search Flights");
        searchBtn.setOnAction(e -> showSearchFlights());
        
        Button bookBtn = createSidebarButton("✈️ Book Ticket");
        bookBtn.setOnAction(e -> showBookTicket());
        
        Button bookingsBtn = createSidebarButton("📋 My Bookings");
        bookingsBtn.setOnAction(e -> showMyBookings());
        
        Button profileBtn = createSidebarButton("👤 Profile");
        profileBtn.setOnAction(e -> showProfile());
        
        sidebar.getChildren().addAll(
            menuLabel, dashboardBtn, searchBtn, bookBtn, bookingsBtn, profileBtn
        );
        
        return sidebar;
    }
    
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-button");
        btn.setPrefWidth(210);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("System", FontWeight.NORMAL, 14));
        return btn;
    }
    
    private void showDashboard() {
        contentArea.getChildren().clear();
        
        Label title = new Label("Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        // Stats cards
        HBox statsBox = new HBox(20);
        
        VBox totalBookings = createStatCard("📊 Total Bookings", 
            String.valueOf(getBookingCount()), "#3b82f6");
        VBox activeBookings = createStatCard("✅ Active Bookings", 
            String.valueOf(getActiveBookingCount()), "#10b981");
        VBox upcomingFlights = createStatCard("🛫 Upcoming Flights", 
            String.valueOf(getUpcomingFlightCount()), "#f59e0b");
        
        statsBox.getChildren().addAll(totalBookings, activeBookings, upcomingFlights);
        
        // Quick actions
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        quickActionsLabel.setStyle("-fx-text-fill: #1e293b;");
        quickActionsLabel.setPadding(new Insets(20, 0, 10, 0));
        
        HBox actionsBox = new HBox(15);
        
        Button searchAction = createActionButton("🔍 Search Flights", "btn btn-primary");
        searchAction.setOnAction(e -> showSearchFlights());
        
        Button bookAction = createActionButton("✈️ Book Ticket", "btn btn-secondary");
        bookAction.setOnAction(e -> showBookTicket());
        
        Button viewAction = createActionButton("📋 View Bookings", "btn btn-outline");
        viewAction.setOnAction(e -> showMyBookings());
        
        actionsBox.getChildren().addAll(searchAction, bookAction, viewAction);
        
        contentArea.getChildren().addAll(title, statsBox, quickActionsLabel, actionsBox);
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        card.setPrefWidth(250);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        titleLabel.setStyle("-fx-text-fill: #64748b;");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private Button createActionButton(String text, String styleClass) {
        Button btn = new Button(text);
        btn.getStyleClass().clear();
        btn.getStyleClass().addAll(styleClass.split(" "));
        btn.setPrefWidth(200);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        return btn;
    }
    
    private void showSearchFlights() {
        contentArea.getChildren().clear();
        SearchFlightsPanel panel = new SearchFlightsPanel(app);
        contentArea.getChildren().add(panel.getView());
    }
    
    private void showBookTicket() {
        contentArea.getChildren().clear();
        BookTicketPanel panel = new BookTicketPanel(app);
        contentArea.getChildren().add(panel.getView());
    }
    
    private void showMyBookings() {
        contentArea.getChildren().clear();
        MyBookingsPanel panel = new MyBookingsPanel(app);
        contentArea.getChildren().add(panel.getView());
    }
    
    private void showProfile() {
        contentArea.getChildren().clear();
        ProfilePanel panel = new ProfilePanel(app);
        contentArea.getChildren().add(panel.getView());
    }
    
    private int getBookingCount() {
        if (app.getCurrentPassenger() == null) return 0;
        List<Booking> bookings = app.getBookingService()
            .getBookingsByPassengerId(app.getCurrentPassenger().getUserId());
        return bookings.size();
    }
    
    private int getActiveBookingCount() {
        if (app.getCurrentPassenger() == null) return 0;
        List<Booking> bookings = app.getBookingService()
            .getBookingsByPassengerId(app.getCurrentPassenger().getUserId());
        return (int) bookings.stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()))
            .count();
    }
    
    private int getUpcomingFlightCount() {
        if (app.getCurrentPassenger() == null) return 0;
        List<Booking> bookings = app.getBookingService()
            .getBookingsByPassengerId(app.getCurrentPassenger().getUserId());
        LocalDateTime now = LocalDateTime.now();
        return (int) bookings.stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()))
            .filter(b -> {
                Flight f = app.getFlightService().getFlightById(b.getFlightId());
                return f != null && f.getDepartureTime().isAfter(now);
            })
            .count();
    }
    
    public void refresh() {
        if (app.getCurrentPassenger() != null) {
            welcomeLabel.setText("Welcome, " + app.getCurrentPassenger().getName());
        }
        showDashboard();
    }
    
    public BorderPane getView() {
        // Recreate the view to avoid scene conflicts
        createView();
        return view;
    }
}

