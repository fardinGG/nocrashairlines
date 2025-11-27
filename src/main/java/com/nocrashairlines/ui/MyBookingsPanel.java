package com.nocrashairlines.ui;

import com.nocrashairlines.exception.BookingException;
import com.nocrashairlines.exception.PaymentException;
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
import java.util.Optional;

public class MyBookingsPanel {

    private final NoCrashAirlinesGUI app;
    private VBox view;
    private VBox bookingsContainer;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public MyBookingsPanel(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
        loadBookings();
    }

    private void createView() {
        view = new VBox(25);
        view.setPadding(new Insets(0));

        Label title = new Label("My Bookings");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                           "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 20;");
        refreshBtn.setOnAction(e -> loadBookings());

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(title, spacer, refreshBtn);

        bookingsContainer = new VBox(15);

        ScrollPane scrollPane = new ScrollPane(bookingsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        view.getChildren().addAll(headerBox, scrollPane);
    }

    private void loadBookings() {
        bookingsContainer.getChildren().clear();

        if (app.getCurrentPassenger() == null) {
            Label error = new Label("Please log in to view bookings");
            error.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 16px;");
            bookingsContainer.getChildren().add(error);
            return;
        }

        List<Booking> bookings = app.getBookingService()
            .getBookingsByPassengerId(app.getCurrentPassenger().getUserId());

        if (bookings.isEmpty()) {
            Label noBookings = new Label("📭 No bookings found. Book your first flight!");
            noBookings.setStyle("-fx-text-fill: #64748b; -fx-font-size: 18px; -fx-padding: 40;");
            bookingsContainer.getChildren().add(noBookings);
            return;
        }

        Label count = new Label("Total Bookings: " + bookings.size());
        count.setFont(Font.font("System", FontWeight.BOLD, 16));
        count.setStyle("-fx-text-fill: #64748b;");
        bookingsContainer.getChildren().add(count);

        for (Booking booking : bookings) {
            VBox bookingCard = createBookingCard(booking);
            bookingsContainer.getChildren().add(bookingCard);
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-border-color: #e2e8f0; -fx-border-width: 2; -fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        // Get flight details
        Flight flight = app.getFlightService().getFlightById(booking.getFlightId());

        // Header with booking ID and status
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label bookingId = new Label("Booking: " + booking.getBookingId());
        bookingId.setFont(Font.font("System", FontWeight.BOLD, 18));
        bookingId.setStyle("-fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(booking.getStatus());
        status.setPadding(new Insets(5, 15, 5, 15));
        status.setStyle(getStatusStyle(booking.getStatus()));

        header.getChildren().addAll(bookingId, spacer, status);

        // Flight information
        if (flight != null) {
            Label flightInfo = new Label("✈️ Flight: " + flight.getFlightNumber());
            flightInfo.setFont(Font.font("System", FontWeight.BOLD, 16));
            flightInfo.setStyle("-fx-text-fill: #2563eb;");

            Label route = new Label("Route: " + flight.getOrigin() + " → " + flight.getDestination());
            route.setFont(Font.font(14));

            Label departure = new Label("Departure: " + flight.getDepartureTime().format(DATE_FORMAT));
            Label arrival = new Label("Arrival: " + flight.getArrivalTime().format(DATE_FORMAT));

            card.getChildren().addAll(header, flightInfo, route, departure, arrival);
        } else {
            card.getChildren().add(header);
        }

        // Booking details
        Separator sep = new Separator();

        GridPane details = new GridPane();
        details.setHgap(30);
        details.setVgap(10);

        details.add(new Label("Passenger:"), 0, 0);
        details.add(new Label(booking.getPassengerName()), 1, 0);

        details.add(new Label("Class:"), 0, 1);
        details.add(new Label(booking.getTravelClass()), 1, 1);

        details.add(new Label("Seat:"), 0, 2);
        details.add(new Label(booking.getSeatNumber()), 1, 2);

        details.add(new Label("Amount:"), 0, 3);
        Label amount = new Label("$" + String.format("%.2f", booking.getTotalAmount()));
        amount.setFont(Font.font("System", FontWeight.BOLD, 14));
        amount.setStyle("-fx-text-fill: #10b981;");
        details.add(amount, 1, 3);

        details.add(new Label("Booked On:"), 0, 4);
        details.add(new Label(booking.getBookingDate().format(DATE_FORMAT)), 1, 4);

        card.getChildren().addAll(sep, details);

        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        if ("CONFIRMED".equals(booking.getStatus())) {
            Button rescheduleBtn = new Button("🔄 Reschedule");
            rescheduleBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; " +
                                  "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 16;");
            rescheduleBtn.setOnAction(e -> rescheduleBooking(booking));

            Button cancelBtn = new Button("❌ Cancel");
            cancelBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                              "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 16;");
            cancelBtn.setOnAction(e -> cancelBooking(booking));

            actions.getChildren().addAll(rescheduleBtn, cancelBtn);
        } else if ("CANCELLED".equals(booking.getStatus())) {
            Label cancelledNote = new Label("This booking has been cancelled");
            cancelledNote.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
            actions.getChildren().add(cancelledNote);
        }

        card.getChildren().add(actions);

        return card;
    }

    private String getStatusStyle(String status) {
        return switch (status) {
            case "CONFIRMED" -> "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 15;";
            case "PENDING" -> "-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 15;";
            case "CANCELLED" -> "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 15;";
            case "RESCHEDULED" -> "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 15;";
            default -> "-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 15;";
        };
    }

    private void rescheduleBooking(Booking booking) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reschedule Booking");
        dialog.setHeaderText("Reschedule Booking: " + booking.getBookingId());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label instruction = new Label("Select a new flight:");
        instruction.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Get available flights
        Flight currentFlight = app.getFlightService().getFlightById(booking.getFlightId());
        if (currentFlight == null) {
            showError("Current flight not found");
            return;
        }

        List<Flight> availableFlights = app.getFlightService()
            .searchFlights(currentFlight.getOrigin(), currentFlight.getDestination(),
                          LocalDateTime.now().plusDays(1));

        ComboBox<String> flightCombo = new ComboBox<>();
        for (Flight f : availableFlights) {
            if (!f.getFlightId().equals(currentFlight.getFlightId()) && f.hasAvailableSeats()) {
                flightCombo.getItems().add(f.getFlightNumber() + " - " +
                    f.getDepartureTime().format(DATE_FORMAT));
            }
        }

        if (flightCombo.getItems().isEmpty()) {
            showError("No alternative flights available");
            return;
        }

        flightCombo.setValue(flightCombo.getItems().get(0));
        flightCombo.setPrefWidth(400);

        content.getChildren().addAll(instruction, flightCombo);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int selectedIndex = flightCombo.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < availableFlights.size()) {
                    Flight newFlight = availableFlights.get(selectedIndex);
                    try {
                        app.getBookingService().rescheduleBooking(booking.getBookingId(), newFlight.getFlightId());
                        showSuccess("Booking rescheduled successfully!");
                        loadBookings();
                    } catch (BookingException e) {
                        showError("Reschedule failed: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void cancelBooking(Booking booking) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel Booking: " + booking.getBookingId());
        confirm.setContentText("Are you sure you want to cancel this booking? A refund will be processed.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                app.getBookingService().cancelBooking(booking.getBookingId());

                // Process refund
                try {
                    app.getPaymentService().processRefund(booking.getBookingId(), "Customer requested cancellation");
                    showSuccess("Booking cancelled and refund processed successfully!");
                } catch (PaymentException e) {
                    showSuccess("Booking cancelled. Refund processing: " + e.getMessage());
                }

                loadBookings();
            } catch (BookingException e) {
                showError("Cancellation failed: " + e.getMessage());
            }
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}

