package com.nocrashairlines.ui;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.model.Admin;
import com.nocrashairlines.model.Passenger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScreen {
    
    private final NoCrashAirlinesGUI app;
    private BorderPane view;
    private TextField emailField;
    private PasswordField passwordField;
    private Label errorLabel;
    private boolean isAdminMode = false;
    
    public LoginScreen(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }
    
    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");

        // Center content
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));
        
        // Login card
        VBox loginCard = new VBox(25);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(450);
        loginCard.setPadding(new Insets(40));
        loginCard.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);");
        
        // Title
        Label title = new Label("Welcome Back");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Sign in to continue");
        subtitle.setFont(Font.font(14));
        subtitle.setStyle("-fx-text-fill: #64748b;");
        
        // Email field
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("Email Address");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        emailLabel.setStyle("-fx-text-fill: #1e293b;");
        
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                           "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 2; " +
                           "-fx-padding: 12; -fx-font-size: 14px;");
        emailField.setOnMouseEntered(e -> emailField.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-border-width: 2; " +
            "-fx-padding: 12; -fx-font-size: 14px;"));
        emailField.setOnMouseExited(e -> {
            if (!emailField.isFocused()) {
                emailField.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 2; " +
                    "-fx-padding: 12; -fx-font-size: 14px;");
            }
        });

        emailBox.getChildren().addAll(emailLabel, emailField);

        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        passwordLabel.setStyle("-fx-text-fill: #1e293b;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                              "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 2; " +
                              "-fx-padding: 12; -fx-font-size: 14px;");
        passwordField.setOnMouseEntered(e -> passwordField.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #3b82f6; -fx-border-radius: 8; -fx-border-width: 2; " +
            "-fx-padding: 12; -fx-font-size: 14px;"));
        passwordField.setOnMouseExited(e -> {
            if (!passwordField.isFocused()) {
                passwordField.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 2; " +
                    "-fx-padding: 12; -fx-font-size: 14px;");
            }
        });
        
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        
        // Login button
        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(370);
        loginBtn.setPrefHeight(50);
        loginBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        loginBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                         "-fx-background-radius: 10; -fx-cursor: hand; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
            "-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 10; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.5), 12, 0, 0, 4);"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
            "-fx-background-color: #3b82f6; -fx-text-fill: white; " +
            "-fx-background-radius: 10; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"));
        loginBtn.setOnAction(e -> handleLogin());

        // Back button
        Button backBtn = new Button("← Back to Home");
        backBtn.setPrefWidth(370);
        backBtn.setPrefHeight(45);
        backBtn.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Store the default style
        final String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #3b82f6; " +
                "-fx-border-color: #3b82f6; -fx-border-width: 2; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;";
        final String hoverStyle = "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; " +
                "-fx-border-color: #3b82f6; -fx-border-width: 2; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;";

        backBtn.setStyle(defaultStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(defaultStyle));

        // Set the action - this is the important part
        final NoCrashAirlinesGUI appRef = this.app;
        backBtn.setOnAction(e -> {
            System.out.println("Back button clicked!");
            appRef.showWelcomeScreen();
        });
        
        // Register link (only for passenger mode)
        HBox registerBox = new HBox(5);
        registerBox.setAlignment(Pos.CENTER);
        Label registerText = new Label("Don't have an account?");
        registerText.setStyle("-fx-text-fill: #64748b;");
        
        Hyperlink registerLink = new Hyperlink("Register here");
        registerLink.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
        registerLink.setOnAction(e -> app.showRegisterScreen());
        
        registerBox.getChildren().addAll(registerText, registerLink);
        
        loginCard.getChildren().addAll(
            title, subtitle, emailBox, passwordBox, errorLabel, loginBtn, backBtn
        );
        
        // Only show register link in passenger mode
        if (!isAdminMode) {
            loginCard.getChildren().add(registerBox);
        }
        
        centerBox.getChildren().add(loginCard);
        view.setCenter(centerBox);
        
        // Enter key support
        passwordField.setOnAction(e -> handleLogin());
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }
        
        try {
            if (isAdminMode) {
                Admin admin = app.getAuthService().loginAdmin(email, password);
                showSuccess("Login successful! Welcome, " + admin.getName());
                app.showAdminDashboard();
            } else {
                Passenger passenger = app.getAuthService().loginPassenger(email, password);
                app.setCurrentPassenger(passenger);
                showSuccess("Login successful! Welcome, " + passenger.getName());
                app.showPassengerDashboard();
            }
        } catch (AuthenticationException e) {
            showError(e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        errorLabel.setVisible(true);
    }
    
    public void setAdminMode(boolean isAdmin) {
        this.isAdminMode = isAdmin;
    }

    public BorderPane getView() {
        // Always recreate the view to ensure fresh state
        createView();
        return view;
    }
}

