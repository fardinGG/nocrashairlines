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
        logoutBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        logoutBtn.setPrefHeight(40);
        logoutBtn.setPrefWidth(100);
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: #dc2626; -fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(239,68,68,0.5), 8, 0, 0, 3);"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: #ef4444; -fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));
        logoutBtn.setOnAction(e -> {
            System.out.println("Logout button clicked");
            app.showWelcomeScreen();
        });
        
        topBar.getChildren().addAll(logo, spacer, logoutBtn);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #1e293b;");

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
        btn.setPrefWidth(210);
        btn.setPrefHeight(45);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("System", FontWeight.NORMAL, 14));

        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; " +
                             "-fx-background-radius: 8; -fx-padding: 10;";
        String hoverStyle = "-fx-background-color: #334155; -fx-text-fill: white; " +
                           "-fx-background-radius: 8; -fx-padding: 10;";

        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));

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
        title.setStyle("-fx-text-fill: #1e293b;");

        // Action buttons
        HBox actionBox = new HBox(15);
        actionBox.setPadding(new Insets(20, 0, 20, 0));

        Button addFlightBtn = new Button("➕ Add New Flight");
        addFlightBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        addFlightBtn.setPrefHeight(45);
        addFlightBtn.setPrefWidth(180);
        addFlightBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                             "-fx-background-radius: 8; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        addFlightBtn.setOnMouseEntered(e -> addFlightBtn.setStyle(
            "-fx-background-color: #059669; -fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.5), 8, 0, 0, 3);"));
        addFlightBtn.setOnMouseExited(e -> addFlightBtn.setStyle(
            "-fx-background-color: #10b981; -fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));
        addFlightBtn.setOnAction(e -> showAddFlightDialog());

        Button viewAllBtn = new Button("📋 View All Flights");
        viewAllBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        viewAllBtn.setPrefHeight(45);
        viewAllBtn.setPrefWidth(180);
        viewAllBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                           "-fx-background-radius: 8; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        viewAllBtn.setOnMouseEntered(e -> viewAllBtn.setStyle(
            "-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.5), 8, 0, 0, 3);"));
        viewAllBtn.setOnMouseExited(e -> viewAllBtn.setStyle(
            "-fx-background-color: #3b82f6; -fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"));
        viewAllBtn.setOnAction(e -> showAllFlights());

        actionBox.getChildren().addAll(addFlightBtn, viewAllBtn);

        // Flight list
        VBox flightList = new VBox(15);
        Label listTitle = new Label("Recent Flights");
        listTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        listTitle.setStyle("-fx-text-fill: #475569;");

        flightList.getChildren().add(listTitle);

        // Show first 5 flights
        app.getFlightService().getAllFlights().stream()
            .limit(5)
            .forEach(flight -> {
                Label flightLabel = new Label(String.format("%s: %s → %s (%s seats available)",
                    flight.getFlightNumber(), flight.getOrigin(), flight.getDestination(),
                    flight.getAvailableSeats()));
                flightLabel.setFont(Font.font(14));
                flightLabel.setStyle("-fx-text-fill: #64748b; -fx-padding: 10; " +
                                    "-fx-background-color: white; -fx-background-radius: 8;");
                flightList.getChildren().add(flightLabel);
            });

        contentArea.getChildren().addAll(title, actionBox, flightList);
    }
    
    private void showBookings() {
        contentArea.getChildren().clear();

        Label title = new Label("All Bookings");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");

        VBox bookingList = new VBox(10);
        bookingList.setPadding(new Insets(20, 0, 0, 0));

        app.getBookingService().getAllBookings().forEach(booking -> {
            VBox bookingCard = new VBox(8);
            bookingCard.setPadding(new Insets(15));
            bookingCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                                "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10;");

            Label bookingId = new Label("Booking: " + booking.getBookingId());
            bookingId.setFont(Font.font("System", FontWeight.BOLD, 16));
            bookingId.setStyle("-fx-text-fill: #2563eb;");

            Label passenger = new Label("Passenger: " + booking.getPassengerName() + " (" + booking.getPassengerId() + ")");
            passenger.setFont(Font.font(14));
            passenger.setStyle("-fx-text-fill: #1e293b;");

            Label flight = new Label("Flight: " + booking.getFlightId());
            flight.setFont(Font.font(14));
            flight.setStyle("-fx-text-fill: #1e293b;");

            Label status = new Label("Status: " + booking.getStatus() + " | Class: " + booking.getTravelClass() +
                                    " | Amount: $" + String.format("%.2f", booking.getTotalAmount()));
            status.setFont(Font.font(12));
            status.setStyle("-fx-text-fill: #64748b;");

            bookingCard.getChildren().addAll(bookingId, passenger, flight, status);
            bookingList.getChildren().add(bookingCard);
        });

        ScrollPane scrollPane = new ScrollPane(bookingList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        contentArea.getChildren().addAll(title, scrollPane);
    }
    
    private void showReports() {
        contentArea.getChildren().clear();

        Label title = new Label("Reports & Analytics");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");

        Label subtitle = new Label("Generate various reports and analytics");
        subtitle.setFont(Font.font(14));
        subtitle.setStyle("-fx-text-fill: #64748b;");

        // Report buttons
        GridPane reportGrid = new GridPane();
        reportGrid.setHgap(15);
        reportGrid.setVgap(15);
        reportGrid.setPadding(new Insets(20, 0, 20, 0));

        // Daily Sales Report
        Button dailySalesBtn = createReportButton("📊 Daily Sales Report",
            "View daily revenue and sales statistics");
        dailySalesBtn.setOnAction(e -> showDailySalesReport());

        // Passenger Trends Report
        Button passengerTrendsBtn = createReportButton("👥 Passenger Trends",
            "Analyze passenger booking patterns");
        passengerTrendsBtn.setOnAction(e -> showPassengerTrendsReport());

        // Most Booked Routes Report
        Button routesBtn = createReportButton("🗺️ Most Booked Routes",
            "See the most popular flight routes");
        routesBtn.setOnAction(e -> showMostBookedRoutesReport());

        // System Statistics Report
        Button systemStatsBtn = createReportButton("📈 System Statistics",
            "Overall system performance metrics");
        systemStatsBtn.setOnAction(e -> showSystemStatisticsReport());

        reportGrid.add(dailySalesBtn, 0, 0);
        reportGrid.add(passengerTrendsBtn, 1, 0);
        reportGrid.add(routesBtn, 0, 1);
        reportGrid.add(systemStatsBtn, 1, 1);

        // Report display area
        TextArea reportArea = new TextArea();
        reportArea.setPrefHeight(400);
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; " +
                           "-fx-background-color: white; -fx-border-color: #e2e8f0; " +
                           "-fx-border-radius: 8; -fx-background-radius: 8;");
        reportArea.setText("Select a report type above to view the report.");

        contentArea.getChildren().addAll(title, subtitle, reportGrid, reportArea);
    }
    
    private Button createReportButton(String title, String description) {
        VBox btnContent = new VBox(5);
        btnContent.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #1e293b;");

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font(12));
        descLabel.setStyle("-fx-text-fill: #64748b;");
        descLabel.setWrapText(true);

        btnContent.getChildren().addAll(titleLabel, descLabel);

        Button btn = new Button();
        btn.setGraphic(btnContent);
        btn.setPrefWidth(280);
        btn.setPrefHeight(80);
        btn.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-border-color: #e2e8f0; -fx-border-width: 2; -fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #f8fafc; -fx-background-radius: 10; " +
            "-fx-border-color: #3b82f6; -fx-border-width: 2; -fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 8, 0, 0, 3);"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white; -fx-background-radius: 10; " +
            "-fx-border-color: #e2e8f0; -fx-border-width: 2; -fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"));

        return btn;
    }

    private void showAddFlightDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Flight");
        dialog.setHeaderText("Enter Flight Details");

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField flightNumberField = new TextField();
        flightNumberField.setPromptText("e.g., NC101");

        TextField originField = new TextField();
        originField.setPromptText("e.g., Toronto");

        TextField destinationField = new TextField();
        destinationField.setPromptText("e.g., Vancouver");

        TextField aircraftField = new TextField();
        aircraftField.setPromptText("e.g., Boeing 737");

        TextField seatsField = new TextField();
        seatsField.setPromptText("e.g., 180");

        TextField depDateField = new TextField();
        depDateField.setPromptText("YYYY-MM-DD");

        TextField depTimeField = new TextField();
        depTimeField.setPromptText("HH:MM (e.g., 10:00)");

        TextField arrDateField = new TextField();
        arrDateField.setPromptText("YYYY-MM-DD");

        TextField arrTimeField = new TextField();
        arrTimeField.setPromptText("HH:MM (e.g., 15:00)");

        grid.add(new Label("Flight Number:"), 0, 0);
        grid.add(flightNumberField, 1, 0);
        grid.add(new Label("Origin:"), 0, 1);
        grid.add(originField, 1, 1);
        grid.add(new Label("Destination:"), 0, 2);
        grid.add(destinationField, 1, 2);
        grid.add(new Label("Aircraft Type:"), 0, 3);
        grid.add(aircraftField, 1, 3);
        grid.add(new Label("Total Seats:"), 0, 4);
        grid.add(seatsField, 1, 4);
        grid.add(new Label("Departure Date:"), 0, 5);
        grid.add(depDateField, 1, 5);
        grid.add(new Label("Departure Time:"), 0, 6);
        grid.add(depTimeField, 1, 6);
        grid.add(new Label("Arrival Date:"), 0, 7);
        grid.add(arrDateField, 1, 7);
        grid.add(new Label("Arrival Time:"), 0, 8);
        grid.add(arrTimeField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String flightNumber = flightNumberField.getText().trim();
                    String origin = originField.getText().trim();
                    String destination = destinationField.getText().trim();
                    String aircraft = aircraftField.getText().trim();
                    int seats = Integer.parseInt(seatsField.getText().trim());

                    java.time.LocalDateTime depTime = java.time.LocalDateTime.parse(
                        depDateField.getText().trim() + "T" + depTimeField.getText().trim());
                    java.time.LocalDateTime arrTime = java.time.LocalDateTime.parse(
                        arrDateField.getText().trim() + "T" + arrTimeField.getText().trim());

                    app.getFlightService().addFlight(flightNumber, origin, destination,
                                                     depTime, arrTime, aircraft, seats);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Flight " + flightNumber + " added successfully!");
                    success.showAndWait();

                    showFlights(); // Refresh the flights view

                } catch (Exception e) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Failed to add flight");
                    error.setContentText(e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void showAllFlights() {
        contentArea.getChildren().clear();

        Label title = new Label("All Flights");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");

        VBox flightList = new VBox(10);
        flightList.setPadding(new Insets(20, 0, 0, 0));

        app.getFlightService().getAllFlights().forEach(flight -> {
            VBox flightCard = new VBox(8);
            flightCard.setPadding(new Insets(15));
            flightCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                               "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10;");

            Label flightNum = new Label(flight.getFlightNumber());
            flightNum.setFont(Font.font("System", FontWeight.BOLD, 16));
            flightNum.setStyle("-fx-text-fill: #2563eb;");

            Label route = new Label(flight.getOrigin() + " → " + flight.getDestination());
            route.setFont(Font.font(14));
            route.setStyle("-fx-text-fill: #1e293b;");

            Label details = new Label(String.format("Seats: %d/%d | Status: %s",
                flight.getAvailableSeats(), flight.getTotalSeats(), flight.getStatus()));
            details.setFont(Font.font(12));
            details.setStyle("-fx-text-fill: #64748b;");

            flightCard.getChildren().addAll(flightNum, route, details);
            flightList.getChildren().add(flightCard);
        });

        ScrollPane scrollPane = new ScrollPane(flightList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        contentArea.getChildren().addAll(title, scrollPane);
    }

    private void showDailySalesReport() {
        TextArea reportArea = getReportTextArea();
        if (reportArea != null) {
            reportArea.setText(app.getAdminService().generateDailySalesReport(
                java.time.LocalDateTime.now()));
        }
    }

    private void showPassengerTrendsReport() {
        TextArea reportArea = getReportTextArea();
        if (reportArea != null) {
            reportArea.setText(app.getAdminService().generatePassengerTrendsReport());
        }
    }

    private void showMostBookedRoutesReport() {
        TextArea reportArea = getReportTextArea();
        if (reportArea != null) {
            reportArea.setText(app.getAdminService().generateMostBookedRoutesReport());
        }
    }

    private void showSystemStatisticsReport() {
        TextArea reportArea = getReportTextArea();
        if (reportArea != null) {
            reportArea.setText(app.getAdminService().generateSystemStatisticsReport());
        }
    }

    private TextArea getReportTextArea() {
        // Find the TextArea in contentArea
        for (javafx.scene.Node node : contentArea.getChildren()) {
            if (node instanceof TextArea) {
                return (TextArea) node;
            }
        }
        return null;
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
        // Recreate the view to avoid scene conflicts
        createView();
        return view;
    }
}

