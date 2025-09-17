package com.budget;

import com.budget.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX Application class for Budget Manager
 */
public class BudgetManagerApp extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database
        DatabaseManager.initializeDatabase();
        
        // Load the main FXML layout
        FXMLLoader fxmlLoader = new FXMLLoader(BudgetManagerApp.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        // Add CSS styling
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        // Configure stage
        stage.setTitle("Budget Manager - Personal Finance Tracker");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        
        // Set application icon
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/budget-icon.png")));
        } catch (Exception e) {
            System.out.println("Could not load application icon: " + e.getMessage());
        }
        
        // Handle application close
        stage.setOnCloseRequest(event -> {
            DatabaseManager.closeConnection();
            System.exit(0);
        });
        
        stage.show();
        
        System.out.println("Budget Manager Application started successfully!");
    }
    
    public static void main(String[] args) {
        launch();
    }
}
