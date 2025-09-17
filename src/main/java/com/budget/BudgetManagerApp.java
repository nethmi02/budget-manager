package com.budget;

import com.budget.database.DatabaseManager;
import com.budget.dao.CategoryDAO;
import com.budget.model.Category;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main JavaFX Application class
 * 
 * JavaFX Concepts:
 * - Application: Base class for JavaFX applications
 * - Stage: Top-level container (window)
 * - Scene: Container for all content in a stage
 * - Nodes: UI components (buttons, labels, etc.)
 */
public class BudgetManagerApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Test database connection
        testDatabase();
        
        // Create a simple UI for now
        VBox root = new VBox(10);
        root.getChildren().add(new Label("Budget Manager Application"));
        root.getChildren().add(new Label("Database connection successful!"));
        root.getChildren().add(new Label("Ready for development..."));
        
        // Create scene and show stage
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Budget Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Handle application shutdown
        primaryStage.setOnCloseRequest(event -> {
            DatabaseManager.getInstance().closeConnection();
        });
    }
    
    /**
     * Test database functionality
     */
    private void testDatabase() {
        try {
            // Initialize database
            DatabaseManager dbManager = DatabaseManager.getInstance();
            System.out.println("Database connected successfully!");
            
            // Test CategoryDAO
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.findAll();
            System.out.println("Found " + categories.size() + " categories:");
            
            for (Category category : categories) {
                System.out.println("- " + category.getName() + " (" + category.getType() + ")");
            }
            
        } catch (Exception e) {
            System.err.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method - entry point for the application
     */
    public static void main(String[] args) {
        // Launch JavaFX application
        launch(args);
    }
}
