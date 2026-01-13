package com.nocrashairlines.ui;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.model.Passenger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RegisterScreen {
    
    private final NoCrashAirlinesGUI app;
    private BorderPane view;
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField phoneField;
    private TextField passportField;
    private Label errorLabel;
    
    public RegisterScreen(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }
    
    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");
        
        // Scrollable center content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));
        
        // Register card
        VBox registerCard = new VBox(20);
        registerCard.setAlignment(Pos.CENTER);
        registerCard.setMaxWidth(500);
        registerCard.setPadding(new Insets(40));
        registerCard.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);");
        
        // Title
        Label title = new Label("Create Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Join NoCrash Airlines today");
        subtitle.setFont(Font.font(14));
        subtitle.setStyle("-fx-text-fill: #64748b;");
        
        // Form fields with inline styling
        String fieldStyle = "-fx-background-color: white; -fx-background-radius: 8; " +
                           "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 2; " +
                           "-fx-padding: 12; -fx-font-size: 14px;";

        nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefHeight(45);
        nameField.setStyle(fieldStyle);

        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(45);
        emailField.setStyle(fieldStyle);

        passwordField = new PasswordField();
        passwordField.setPromptText("Min 8 chars, 1 number, 1 special char");
        passwordField.setPrefHeight(45);
        passwordField.setStyle(fieldStyle);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter your password");
        confirmPasswordField.setPrefHeight(45);
        confirmPasswordField.setStyle(fieldStyle);

        phoneField = new TextField();
        phoneField.setPromptText("+1234567890");
        phoneField.setPrefHeight(45);
        phoneField.setStyle(fieldStyle);

        passportField = new TextField();
        passportField.setPromptText("AB123456");
        passportField.setPrefHeight(45);
        passportField.setStyle(fieldStyle);
        
        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        
        // Register button
        Button registerBtn = new Button("Create Account");
        registerBtn.setPrefWidth(420);
        registerBtn.setPrefHeight(50);
        registerBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        registerBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(
            "-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.5), 12, 0, 0, 4);"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle(
            "-fx-background-color: #3b82f6; -fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"));
        registerBtn.setOnAction(e -> handleRegister());

        // Back button
        Button backBtn = new Button("← Back to Login");
        backBtn.setPrefWidth(420);
        backBtn.setPrefHeight(45);
        backBtn.setFont(Font.font("System", FontWeight.BOLD, 14));

        final String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #3b82f6; " +
                "-fx-border-color: #3b82f6; -fx-border-width: 2; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;";
        final String hoverStyle = "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; " +
                "-fx-border-color: #3b82f6; -fx-border-width: 2; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;";

        backBtn.setStyle(defaultStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(defaultStyle));

        final NoCrashAirlinesGUI appRef = this.app;
        backBtn.setOnAction(e -> {
            System.out.println("Back to Login button clicked!");
            appRef.showLoginScreen(false);
        });
        
        registerCard.getChildren().addAll(
            title, subtitle,
            nameField, emailField, passwordField, confirmPasswordField,
            phoneField, passportField,
            errorLabel, registerBtn, backBtn
        );
        
        centerBox.getChildren().add(registerCard);
        scrollPane.setContent(centerBox);
        view.setCenter(scrollPane);
    }
    
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String passport = passportField.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || 
            phone.isEmpty() || passport.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        try {
            Passenger passenger = app.getAuthService().registerPassenger(
                name, email, password, phone, passport
            );
            
            showSuccess("Registration successful! Please login.");
            
            // Wait a moment then go to login
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> app.showLoginScreen(false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (AuthenticationException e) {
            showError(e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        errorLabel.setVisible(true);
    }
    
    public BorderPane getView() {
        // Always recreate the view to ensure fresh state
        createView();
        return view;
    }
}

