package com.nocrashairlines.ui;

import com.nocrashairlines.exception.AuthenticationException;
import com.nocrashairlines.model.Passenger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;

public class ProfilePanel {

    private final NoCrashAirlinesGUI app;
    private VBox view;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField passportField;
    private TextField addressField;
    private ComboBox<String> preferredClassCombo;
    private Label accountInfoLabel;
    private boolean editMode = false;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public ProfilePanel(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }

    private void createView() {
        view = new VBox(25);
        view.setPadding(new Insets(0));

        Label title = new Label("My Profile");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");

        // Profile card
        VBox profileCard = new VBox(20);
        profileCard.setPadding(new Insets(30));
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");

        // Account information section
        VBox accountSection = createAccountSection();

        // Personal information section
        VBox personalSection = createPersonalInfoSection();

        // Action buttons
        HBox actionButtons = createActionButtons();

        profileCard.getChildren().addAll(accountSection, new Separator(), personalSection, actionButtons);

        view.getChildren().addAll(title, profileCard);
    }

    private VBox createAccountSection() {
        VBox section = new VBox(15);

        Label sectionTitle = new Label("Account Information");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setStyle("-fx-text-fill: #1e293b;");

        accountInfoLabel = new Label();
        accountInfoLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        updateAccountInfo();

        section.getChildren().addAll(sectionTitle, accountInfoLabel);
        return section;
    }

    private VBox createPersonalInfoSection() {
        VBox section = new VBox(15);

        Label sectionTitle = new Label("Personal Information");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setStyle("-fx-text-fill: #1e293b;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));

        // Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameField = new TextField();
        nameField.setPrefHeight(40);
        nameField.setEditable(false);
        nameField.setStyle("-fx-background-color: #f8fafc;");

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        emailField = new TextField();
        emailField.setPrefHeight(40);
        emailField.setEditable(false);
        emailField.setStyle("-fx-background-color: #f8fafc;");

        // Phone
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        phoneField = new TextField();
        phoneField.setPrefHeight(40);
        phoneField.setEditable(false);
        phoneField.setStyle("-fx-background-color: #f8fafc;");

        // Passport
        Label passportLabel = new Label("Passport Number:");
        passportLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        passportField = new TextField();
        passportField.setPrefHeight(40);
        passportField.setEditable(false);
        passportField.setStyle("-fx-background-color: #f8fafc;");

        // Address
        Label addressLabel = new Label("Address:");
        addressLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        addressField = new TextField();
        addressField.setPrefHeight(40);
        addressField.setEditable(false);
        addressField.setStyle("-fx-background-color: #f8fafc;");

        // Preferred Class
        Label classLabel = new Label("Preferred Class:");
        classLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        preferredClassCombo = new ComboBox<>();
        preferredClassCombo.getItems().addAll("ECONOMY", "BUSINESS", "FIRST_CLASS");
        preferredClassCombo.setPrefHeight(40);
        preferredClassCombo.setDisable(true);
        preferredClassCombo.setStyle("-fx-background-color: #f8fafc;");

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(passportLabel, 0, 3);
        grid.add(passportField, 1, 3);
        grid.add(addressLabel, 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(classLabel, 0, 5);
        grid.add(preferredClassCombo, 1, 5);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        loadProfileData();

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private HBox createActionButtons() {
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button editBtn = new Button("✏️ Edit Profile");
        editBtn.setPrefHeight(45);
        editBtn.setPrefWidth(150);
        editBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
        editBtn.setOnAction(e -> toggleEditMode());

        Button saveBtn = new Button("💾 Save Changes");
        saveBtn.setPrefHeight(45);
        saveBtn.setPrefWidth(150);
        saveBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        saveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setVisible(false);
        saveBtn.setOnAction(e -> saveProfile());

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.setPrefHeight(45);
        cancelBtn.setPrefWidth(150);
        cancelBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        cancelBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-cursor: hand;");
        cancelBtn.setVisible(false);
        cancelBtn.setOnAction(e -> {
            toggleEditMode();
            loadProfileData();
        });

        Button changePasswordBtn = new Button("🔒 Change Password");
        changePasswordBtn.setPrefHeight(45);
        changePasswordBtn.setPrefWidth(180);
        changePasswordBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        changePasswordBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; " +
                                   "-fx-background-radius: 8; -fx-cursor: hand;");
        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());

        buttons.getChildren().addAll(editBtn, saveBtn, cancelBtn, changePasswordBtn);

        // Store references for toggling visibility
        editBtn.setUserData(new Button[]{saveBtn, cancelBtn});

        return buttons;
    }

    private void updateAccountInfo() {
        if (app.getCurrentPassenger() != null) {
            Passenger p = app.getCurrentPassenger();
            StringBuilder info = new StringBuilder();
            info.append("User ID: ").append(p.getUserId()).append("\n");
            info.append("Account Created: ").append(p.getCreatedAt().format(DATE_FORMAT)).append("\n");
            if (p.getLastLogin() != null) {
                info.append("Last Login: ").append(p.getLastLogin().format(DATE_FORMAT));
            }
            accountInfoLabel.setText(info.toString());
        }
    }

    private void loadProfileData() {
        if (app.getCurrentPassenger() != null) {
            Passenger p = app.getCurrentPassenger();
            nameField.setText(p.getName());
            emailField.setText(p.getEmail());
            phoneField.setText(p.getPhoneNumber());
            passportField.setText(p.getPassportNumber());
            addressField.setText(p.getAddress() != null ? p.getAddress() : "");
            preferredClassCombo.setValue(p.getPreferredClass() != null ? p.getPreferredClass() : "ECONOMY");
        }
    }

    private void toggleEditMode() {
        editMode = !editMode;

        nameField.setEditable(editMode);
        phoneField.setEditable(editMode);
        addressField.setEditable(editMode);
        preferredClassCombo.setDisable(!editMode);

        String bgColor = editMode ? "white" : "#f8fafc";
        nameField.setStyle("-fx-background-color: " + bgColor + ";");
        phoneField.setStyle("-fx-background-color: " + bgColor + ";");
        addressField.setStyle("-fx-background-color: " + bgColor + ";");
        preferredClassCombo.setStyle("-fx-background-color: " + bgColor + ";");

        // Toggle button visibility
        HBox buttonBox = (HBox) view.lookup(".hbox");
        if (buttonBox != null) {
            for (var node : buttonBox.getChildren()) {
                if (node instanceof Button btn) {
                    if (btn.getText().contains("Edit")) {
                        btn.setVisible(!editMode);
                    } else if (btn.getText().contains("Save") || btn.getText().contains("Cancel")) {
                        btn.setVisible(editMode);
                    }
                }
            }
        }
    }

    private void saveProfile() {
        if (app.getCurrentPassenger() == null) {
            showError("No passenger logged in");
            return;
        }

        Passenger p = app.getCurrentPassenger();
        p.setName(nameField.getText().trim());
        p.setPhoneNumber(phoneField.getText().trim());
        p.setAddress(addressField.getText().trim());
        p.setPreferredClass(preferredClassCombo.getValue());

        // Update in database
        if (app.getAuthService().updatePassengerProfile(p)) {
            showSuccess("Profile updated successfully!");
            toggleEditMode();
        } else {
            showError("Failed to update profile");
        }
    }

    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change Your Password");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Current Password");
        currentPassword.setPrefHeight(40);

        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");
        newPassword.setPrefHeight(40);

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm New Password");
        confirmPassword.setPrefHeight(40);

        Label hint = new Label("Password must be at least 8 characters with uppercase, lowercase, and number");
        hint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px; -fx-font-style: italic;");
        hint.setWrapText(true);

        content.getChildren().addAll(
            new Label("Current Password:"), currentPassword,
            new Label("New Password:"), newPassword,
            new Label("Confirm Password:"), confirmPassword,
            hint
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String current = currentPassword.getText();
                String newPass = newPassword.getText();
                String confirm = confirmPassword.getText();

                if (!newPass.equals(confirm)) {
                    showError("New passwords do not match");
                    return;
                }

                try {
                    if (app.getAuthService().changePassword(app.getCurrentPassenger(), current, newPass)) {
                        showSuccess("Password changed successfully!");
                    } else {
                        showError("Failed to change password");
                    }
                } catch (AuthenticationException e) {
                    showError("Password change failed: " + e.getMessage());
                }
            }
        });
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

