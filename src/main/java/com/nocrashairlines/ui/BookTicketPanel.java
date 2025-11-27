package com.nocrashairlines.ui;

import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.exception.PaymentException;
import com.nocrashairlines.model.Booking;
import com.nocrashairlines.model.Flight;
import com.nocrashairlines.model.Payment;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookTicketPanel {

    private final NoCrashAirlinesGUI app;
    private VBox view;
    private TextField originField;
    private TextField destinationField;
    private Spinner<Integer> daysSpinner;
    private VBox resultsBox;
    private Flight selectedFlight;
    private String selectedClass = "ECONOMY";

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public BookTicketPanel(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }

    private void createView() {
        view = new VBox(25);
        view.setPadding(new Insets(0));

        Label title = new Label("Book a Flight");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");

        // Search form
        VBox searchCard = createSearchCard();

        // Results area
        resultsBox = new VBox(15);

        view.getChildren().addAll(title, searchCard, resultsBox);
    }

    private VBox createSearchCard() {
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
        originBox.getChildren().addAll(originLabel, originField);

        VBox destBox = new VBox(8);
        Label destLabel = new Label("Destination City");
        destLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        destinationField = new TextField();
        destinationField.setPromptText("e.g., Vancouver");
        destinationField.setPrefHeight(45);
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
        searchBtn.setPrefHeight(50);
        searchBtn.setPrefWidth(200);
        searchBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        searchBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> searchFlights());

        searchCard.getChildren().addAll(hint, fieldsBox, searchBtn);
        return searchCard;
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

        Label resultsTitle = new Label("Available Flights");
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
            VBox flightCard = createBookableFlightCard(flight);
            resultsBox.getChildren().add(flightCard);
        }
    }

    private VBox createBookableFlightCard(Flight flight) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label flightNumber = new Label(flight.getFlightNumber());
        flightNumber.setFont(Font.font("System", FontWeight.BOLD, 20));
        flightNumber.setStyle("-fx-text-fill: #2563eb;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(flight.getStatus());
        status.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                       "-fx-padding: 5 15; -fx-background-radius: 15;");

        header.getChildren().addAll(flightNumber, spacer, status);

        // Route and times
        Label route = new Label(flight.getOrigin() + " → " + flight.getDestination());
        route.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox times = new HBox(30);
        Label depTime = new Label("Departs: " + flight.getDepartureTime().format(DATE_FORMAT));
        Label arrTime = new Label("Arrives: " + flight.getArrivalTime().format(DATE_FORMAT));
        Label seats = new Label("Seats: " + flight.getAvailableSeats() + "/" + flight.getTotalSeats());
        times.getChildren().addAll(depTime, arrTime, seats);

        // Class selection
        Label classLabel = new Label("Select Travel Class:");
        classLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        ToggleGroup classGroup = new ToggleGroup();
        HBox classBox = new HBox(15);

        RadioButton economyBtn = new RadioButton("Economy - $" + flight.getClassPrice("ECONOMY"));
        economyBtn.setToggleGroup(classGroup);
        economyBtn.setSelected(true);
        economyBtn.setUserData("ECONOMY");

        RadioButton businessBtn = new RadioButton("Business - $" + flight.getClassPrice("BUSINESS"));
        businessBtn.setToggleGroup(classGroup);
        businessBtn.setUserData("BUSINESS");

        RadioButton firstBtn = new RadioButton("First Class - $" + flight.getClassPrice("FIRST_CLASS"));
        firstBtn.setToggleGroup(classGroup);
        firstBtn.setUserData("FIRST_CLASS");

        classBox.getChildren().addAll(economyBtn, businessBtn, firstBtn);

        // Book button
        Button bookBtn = new Button("✈️ Book This Flight");
        bookBtn.setPrefHeight(45);
        bookBtn.setPrefWidth(200);
        bookBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        bookBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
        bookBtn.setOnAction(e -> {
            selectedFlight = flight;
            selectedClass = (String) classGroup.getSelectedToggle().getUserData();
            bookFlight();
        });

        card.getChildren().addAll(header, route, times, classLabel, classBox, bookBtn);
        return card;
    }

    private void bookFlight() {
        if (selectedFlight == null || app.getCurrentPassenger() == null) {
            showError("Please select a flight and ensure you are logged in");
            return;
        }

        try {
            // Create booking
            Booking booking = app.getBookingService().createBooking(
                app.getCurrentPassenger().getUserId(),
                selectedFlight.getFlightId(),
                app.getCurrentPassenger().getName(),
                app.getCurrentPassenger().getEmail(),
                app.getCurrentPassenger().getPhoneNumber(),
                app.getCurrentPassenger().getPassportNumber(),
                selectedClass
            );

            // Show payment dialog
            showPaymentDialog(booking);

        } catch (BookingException e) {
            showError("Booking failed: " + e.getMessage());
        }
    }

    private void showPaymentDialog(Booking booking) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Payment");
        dialog.setHeaderText("Complete Payment for Booking " + booking.getBookingId());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label amountLabel = new Label("Amount: $" + String.format("%.2f", booking.getTotalAmount()));
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        ComboBox<String> paymentMethod = new ComboBox<>();
        paymentMethod.getItems().addAll("CREDIT_CARD", "DEBIT_CARD", "DIGITAL_WALLET", "ONLINE_BANKING");
        paymentMethod.setValue("CREDIT_CARD");
        paymentMethod.setPrefWidth(300);

        TextField cardField = new TextField();
        cardField.setPromptText("Card Last 4 Digits");
        cardField.setPrefWidth(300);

        content.getChildren().addAll(
            new Label("Booking Details:"),
            new Label("Flight: " + selectedFlight.getFlightNumber()),
            new Label("Route: " + selectedFlight.getOrigin() + " → " + selectedFlight.getDestination()),
            new Label("Class: " + selectedClass),
            new Label("Seat: " + booking.getSeatNumber()),
            new Separator(),
            amountLabel,
            new Label("Payment Method:"),
            paymentMethod,
            new Label("Card Information:"),
            cardField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                processPayment(booking, paymentMethod.getValue(), cardField.getText());
            }
        });
    }

    private void processPayment(Booking booking, String paymentMethod, String cardDigits) {
        try {
            Payment payment = app.getPaymentService().processPayment(
                booking.getBookingId(),
                app.getCurrentPassenger().getUserId(),
                paymentMethod,
                cardDigits
            );

            showSuccess("Booking confirmed! Your booking ID is: " + booking.getBookingId() +
                       "\nPayment ID: " + payment.getPaymentId() +
                       "\nE-ticket has been sent to your email.");

            // Clear search
            resultsBox.getChildren().clear();
            originField.clear();
            destinationField.clear();

        } catch (PaymentException | BookingException e) {
            showError("Payment failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Booking Successful!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}

