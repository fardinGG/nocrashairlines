package com.nocrashairlines.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MyBookingsPanel {
    
    private final NoCrashAirlinesGUI app;
    private VBox view;
    
    public MyBookingsPanel(NoCrashAirlinesGUI app) {
        this.app = app;
        createView();
    }
    
    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(0));
        
        Label title = new Label("My Bookings");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        Label comingSoon = new Label("Bookings list coming soon...");
        comingSoon.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");
        
        view.getChildren().addAll(title, comingSoon);
    }
    
    public VBox getView() {
        return view;
    }
}

