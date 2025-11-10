package com.nocrashairlines.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminDashboard {
    
    private final NoCrashAirlinesGUI app;
    private BorderPane view;
    private VBox contentArea;
    
    public AdminDashboard(NoCrashAirlinesGUI app) {
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
        
        showDashboard();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(20, 30, 20, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label logo = new Label("✈ NoCrash Airlines - Admin Portal");
        logo.setFont(Font.font("System", FontWeight.BOLD, 24));
        logo.setStyle("-fx-text-fill: #2563eb;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().addAll("btn", "btn-danger");
        logoutBtn.setOnAction(e -> app.showWelcomeScreen());
        
        topBar.getChildren().addAll(logo, spacer, logoutBtn);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.getStyleClass().add("sidebar");
        
        Label menuLabel = new Label("ADMIN MENU");
        menuLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        menuLabel.setStyle("-fx-text-fill: #94a3b8;");
        menuLabel.setPadding(new Insets(10, 0, 10, 10));
        
        Button dashboardBtn = createSidebarButton("📊 Dashboard");
        dashboardBtn.setOnAction(e -> showDashboard());
        
        Button flightsBtn = createSidebarButton("✈️ Manage Flights");
        flightsBtn.setOnAction(e -> showFlights());
        
        Button bookingsBtn = createSidebarButton("📋 All Bookings");
        bookingsBtn.setOnAction(e -> showBookings());
        
        Button reportsBtn = createSidebarButton("📈 Reports");
        reportsBtn.setOnAction(e -> showReports());
        
        sidebar.getChildren().addAll(menuLabel, dashboardBtn, flightsBtn, bookingsBtn, reportsBtn);
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
        
        Label title = new Label("Admin Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("System Overview and Statistics");
        subtitle.setFont(Font.font(14));
        subtitle.setStyle("-fx-text-fill: #64748b;");
        
        // Stats
        HBox statsBox = new HBox(20);
        statsBox.setPadding(new Insets(20, 0, 0, 0));
        
        VBox totalFlights = createStatCard("✈️ Total Flights", 
            String.valueOf(app.getFlightService().getAllFlights().size()), "#3b82f6");
        VBox totalBookings = createStatCard("📋 Total Bookings", 
            String.valueOf(app.getBookingService().getAllBookings().size()), "#10b981");
        VBox totalRevenue = createStatCard("💰 Total Revenue", 
            "$" + String.format("%.2f", calculateRevenue()), "#f59e0b");
        
        statsBox.getChildren().addAll(totalFlights, totalBookings, totalRevenue);
        
        contentArea.getChildren().addAll(title, subtitle, statsBox);
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
    
    private void showFlights() {
        contentArea.getChildren().clear();
        Label title = new Label("Manage Flights");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        contentArea.getChildren().add(title);
        // TODO: Add flight management UI
    }
    
    private void showBookings() {
        contentArea.getChildren().clear();
        Label title = new Label("All Bookings");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        contentArea.getChildren().add(title);
        // TODO: Add bookings UI
    }
    
    private void showReports() {
        contentArea.getChildren().clear();
        Label title = new Label("Reports & Analytics");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        contentArea.getChildren().add(title);
        
        TextArea reportArea = new TextArea();
        reportArea.setPrefHeight(500);
        reportArea.setEditable(false);
        reportArea.setText(app.getAdminService().generateSystemStatisticsReport());
        
        contentArea.getChildren().add(reportArea);
    }
    
    private double calculateRevenue() {
        try {
            return app.getPaymentService().getAllPayments().stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount())
                .sum();
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    public void refresh() {
        showDashboard();
    }
    
    public BorderPane getView() {
        // Refresh the dashboard when showing
        refresh();
        return view;
    }
}

