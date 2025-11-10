package com.nocrashairlines.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class WelcomeScreen {
    
    private final NoCrashAirlinesGUI app;
    private BorderPane view;
    
    public WelcomeScreen(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }
    
    private void createView() {
        view = new BorderPane();
        // Gradient background
        view.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");

        // Center content
        VBox centerBox = new VBox(40);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        // Logo/Title
        Label title = new Label("✈ NoCrash Airlines");
        title.setFont(Font.font("System", FontWeight.BOLD, 56));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");

        Label subtitle = new Label("Your Journey, Our Priority");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 24));
        subtitle.setStyle("-fx-text-fill: white;");

        // Welcome message
        Label welcomeMsg = new Label("Book flights, manage bookings, and travel with confidence");
        welcomeMsg.setFont(Font.font("System", 16));
        welcomeMsg.setStyle("-fx-text-fill: white;");

        // Buttons container
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(400);

        // Passenger Login Button
        Button passengerBtn = new Button("Passenger Login");
        passengerBtn.setPrefWidth(350);
        passengerBtn.setPrefHeight(60);
        passengerBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        passengerBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                             "-fx-background-radius: 10; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        passengerBtn.setOnMouseEntered(e -> passengerBtn.setStyle(
            "-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.5), 12, 0, 0, 4);"));
        passengerBtn.setOnMouseExited(e -> passengerBtn.setStyle(
            "-fx-background-color: #3b82f6; -fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"));
        passengerBtn.setOnAction(e -> app.showLoginScreen(false));

        // Passenger Register Button
        Button registerBtn = new Button("New Passenger? Register Here");
        registerBtn.setPrefWidth(350);
        registerBtn.setPrefHeight(60);
        registerBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        registerBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(
            "-fx-background-color: #059669; -fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.5), 12, 0, 0, 4);"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle(
            "-fx-background-color: #10b981; -fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"));
        registerBtn.setOnAction(e -> app.showRegisterScreen());

        // Admin Login Button
        Button adminBtn = new Button("Admin Portal");
        adminBtn.setPrefWidth(350);
        adminBtn.setPrefHeight(50);
        adminBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        adminBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                         "-fx-border-color: white; -fx-border-width: 2; " +
                         "-fx-background-radius: 10; -fx-border-radius: 10;");
        adminBtn.setOnMouseEntered(e -> adminBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
            "-fx-border-color: white; -fx-border-width: 2; " +
            "-fx-background-radius: 10; -fx-border-radius: 10;"));
        adminBtn.setOnMouseExited(e -> adminBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; " +
            "-fx-border-color: white; -fx-border-width: 2; " +
            "-fx-background-radius: 10; -fx-border-radius: 10;"));
        adminBtn.setOnAction(e -> app.showLoginScreen(true));

        buttonBox.getChildren().addAll(passengerBtn, registerBtn, adminBtn);
        
        // Features section
        HBox features = createFeaturesBox();
        
        centerBox.getChildren().addAll(title, subtitle, welcomeMsg, buttonBox, features);
        
        view.setCenter(centerBox);
    }
    
    private HBox createFeaturesBox() {
        HBox featuresBox = new HBox(40);
        featuresBox.setAlignment(Pos.CENTER);
        featuresBox.setPadding(new Insets(30, 0, 0, 0));
        
        VBox feature1 = createFeature("🔍", "Search Flights", "Find the best flights for your journey");
        VBox feature2 = createFeature("💳", "Easy Booking", "Book and pay in just a few clicks");
        VBox feature3 = createFeature("📱", "Manage Bookings", "View, cancel, or reschedule anytime");
        
        featuresBox.getChildren().addAll(feature1, feature2, feature3);
        return featuresBox;
    }
    
    private VBox createFeature(String icon, String title, String description) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(200);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(40));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font(12));
        descLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        
        box.getChildren().addAll(iconLabel, titleLabel, descLabel);
        return box;
    }
    
    public BorderPane getView() {
        // Always recreate the view to ensure fresh state
        createView();
        return view;
    }
}

