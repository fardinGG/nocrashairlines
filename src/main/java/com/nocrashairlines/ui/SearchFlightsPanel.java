package com.nocrashairlines.ui;

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

public class SearchFlightsPanel {
    
    private final NoCrashAirlinesGUI app;
    private VBox view;
    private TextField originField;
    private TextField destinationField;
    private Spinner<Integer> daysSpinner;
    private VBox resultsBox;
    
    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    public SearchFlightsPanel(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }
    
    private void createView() {
        view = new VBox(25);
        view.setPadding(new Insets(0));
        
        Label title = new Label("Search Flights");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        // Search form
        VBox searchCard = new VBox(20);
        searchCard.setPadding(new Insets(30));
        searchCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        
        Label hint = new Label("Available cities: Toronto, Vancouver, Montreal, Calgary");
        hint.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
        
        HBox fieldsBox = new HBox(15);
        
        VBox originBox = new VBox(8);
        Label originLabel = new Label("Origin City");
        originLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        originField = new TextField();
        originField.setPromptText("e.g., Toronto");
        originField.setPrefHeight(45);
        originField.getStyleClass().add("text-field");
        originBox.getChildren().addAll(originLabel, originField);
        
        VBox destBox = new VBox(8);
        Label destLabel = new Label("Destination City");
        destLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        destinationField = new TextField();
        destinationField.setPromptText("e.g., Vancouver");
        destinationField.setPrefHeight(45);
        destinationField.getStyleClass().add("text-field");
        destBox.getChildren().addAll(destLabel, destinationField);
        
        VBox daysBox = new VBox(8);
        Label daysLabel = new Label("Days from Today");
        daysLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        daysSpinner = new Spinner<>(0, 365, 1);
        daysSpinner.setPrefHeight(45);
        daysSpinner.setEditable(true);
        daysBox.getChildren().addAll(daysLabel, daysSpinner);
        
        HBox.setHgrow(originBox, Priority.ALWAYS);
        HBox.setHgrow(destBox, Priority.ALWAYS);
        fieldsBox.getChildren().addAll(originBox, destBox, daysBox);
        
        Button searchBtn = new Button("🔍 Search Flights");
        searchBtn.getStyleClass().addAll("btn", "btn-primary");
        searchBtn.setPrefHeight(50);
        searchBtn.setPrefWidth(200);
        searchBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        searchBtn.setOnAction(e -> searchFlights());
        
        searchCard.getChildren().addAll(hint, fieldsBox, searchBtn);
        
        // Results area
        resultsBox = new VBox(15);
        
        view.getChildren().addAll(title, searchCard, resultsBox);
    }
    
    private void searchFlights() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        int days = daysSpinner.getValue();
        
        if (origin.isEmpty() || destination.isEmpty()) {
            showError("Please enter both origin and destination");
            return;
        }
        
        LocalDateTime searchDate = LocalDateTime.now().plusDays(days);
        List<Flight> flights = app.getFlightService().searchFlights(origin, destination, searchDate);
        
        displayResults(flights, origin, destination, searchDate);
    }
    
    private void displayResults(List<Flight> flights, String origin, String destination, LocalDateTime date) {
        resultsBox.getChildren().clear();
        
        Label resultsTitle = new Label("Search Results");
        resultsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        resultsTitle.setStyle("-fx-text-fill: #1e293b;");
        
        if (flights.isEmpty()) {
            Label noResults = new Label("❌ No flights found for " + origin + " → " + destination + 
                                       " on " + date.toLocalDate());
            noResults.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");
            resultsBox.getChildren().addAll(resultsTitle, noResults);
            return;
        }
        
        Label foundLabel = new Label("✅ Found " + flights.size() + " flight(s)");
        foundLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        resultsBox.getChildren().addAll(resultsTitle, foundLabel);
        
        for (Flight flight : flights) {
            VBox flightCard = createFlightCard(flight);
            resultsBox.getChildren().add(flightCard);
        }
    }
    
    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.getStyleClass().add("flight-card");
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label flightNumber = new Label(flight.getFlightNumber());
        flightNumber.setFont(Font.font("System", FontWeight.BOLD, 20));
        flightNumber.setStyle("-fx-text-fill: #2563eb;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label status = new Label(flight.getStatus());
        status.getStyleClass().addAll("badge", "badge-success");
        
        header.getChildren().addAll(flightNumber, spacer, status);
        
        // Route
        HBox route = new HBox(10);
        route.setAlignment(Pos.CENTER_LEFT);
        
        Label routeLabel = new Label(flight.getOrigin() + " → " + flight.getDestination());
        routeLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        routeLabel.setStyle("-fx-text-fill: #1e293b;");
        
        route.getChildren().add(routeLabel);
        
        // Times
        HBox times = new HBox(30);
        
        VBox depBox = new VBox(5);
        Label depLabel = new Label("Departure");
        depLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Label depTime = new Label(flight.getDepartureTime().format(DATE_FORMAT));
        depTime.setFont(Font.font("System", FontWeight.BOLD, 14));
        depBox.getChildren().addAll(depLabel, depTime);
        
        VBox arrBox = new VBox(5);
        Label arrLabel = new Label("Arrival");
        arrLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Label arrTime = new Label(flight.getArrivalTime().format(DATE_FORMAT));
        arrTime.setFont(Font.font("System", FontWeight.BOLD, 14));
        arrBox.getChildren().addAll(arrLabel, arrTime);
        
        VBox seatsBox = new VBox(5);
        Label seatsLabel = new Label("Available Seats");
        seatsLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Label seatsCount = new Label(flight.getAvailableSeats() + "/" + flight.getTotalSeats());
        seatsCount.setFont(Font.font("System", FontWeight.BOLD, 14));
        seatsBox.getChildren().addAll(seatsLabel, seatsCount);
        
        times.getChildren().addAll(depBox, arrBox, seatsBox);
        
        // Prices
        HBox prices = new HBox(20);
        prices.setAlignment(Pos.CENTER_LEFT);
        
        Label pricesLabel = new Label("Prices:");
        pricesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label economy = new Label("Economy: $" + flight.getClassPrice("ECONOMY"));
        economy.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        
        Label business = new Label("Business: $" + flight.getClassPrice("BUSINESS"));
        business.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        
        Label firstClass = new Label("First Class: $" + flight.getClassPrice("FIRST_CLASS"));
        firstClass.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        
        prices.getChildren().addAll(pricesLabel, economy, business, firstClass);
        
        card.getChildren().addAll(header, route, times, prices);
        return card;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public VBox getView() {
        return view;
    }
}

