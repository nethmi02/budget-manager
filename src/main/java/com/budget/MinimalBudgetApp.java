package com.budget;

import com.budget.database.DatabaseManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Minimal Budget Manager Application - Simplified version to ensure UI shows
 */
public class MinimalBudgetApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            DatabaseManager.initializeDatabase();
            System.out.println("✅ Database initialized successfully!");
            
            // Create simple layout
            VBox root = new VBox(20);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f5f5f5;");
            
            // Title
            Label title = new Label("💰 Budget Manager");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            // Status
            Label status = new Label("✅ Application is running successfully!");
            status.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
            
            // Database info
            Label dbInfo = new Label("🗄️ Database: Connected and ready");
            dbInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            
            // Feature buttons
            HBox buttonBox = new HBox(10);
            
            Button expenseBtn = new Button("💸 Add Expense");
            expenseBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
            expenseBtn.setOnAction(e -> showAlert("Add Expense", "Expense feature ready to implement!"));
            
            Button incomeBtn = new Button("💰 Add Income");
            incomeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px;");
            incomeBtn.setOnAction(e -> showAlert("Add Income", "Income feature ready to implement!"));
            
            Button budgetBtn = new Button("📊 View Budget");
            budgetBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px;");
            budgetBtn.setOnAction(e -> showAlert("View Budget", "Budget feature ready to implement!"));
            
            buttonBox.getChildren().addAll(expenseBtn, incomeBtn, budgetBtn);
            
            // Summary area
            VBox summaryBox = new VBox(10);
            summaryBox.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
            
            Label summaryTitle = new Label("📈 Financial Summary");
            summaryTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            GridPane summaryGrid = new GridPane();
            summaryGrid.setHgap(20);
            summaryGrid.setVgap(10);
            
            summaryGrid.add(new Label("Total Income:"), 0, 0);
            summaryGrid.add(new Label("$0.00"), 1, 0);
            
            summaryGrid.add(new Label("Total Expenses:"), 0, 1);
            summaryGrid.add(new Label("$0.00"), 1, 1);
            
            summaryGrid.add(new Label("Net Balance:"), 0, 2);
            Label netLabel = new Label("$0.00");
            netLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            summaryGrid.add(netLabel, 1, 2);
            
            summaryBox.getChildren().addAll(summaryTitle, summaryGrid);
            
            // Instructions
            Label instructions = new Label("🚀 Your Budget Manager is ready! Click the buttons above to start managing your finances.");
            instructions.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-wrap-text: true;");
            
            // Add all components
            root.getChildren().addAll(title, status, dbInfo, new Separator(), buttonBox, summaryBox, instructions);
            
            // Create scene with fixed size
            Scene scene = new Scene(root, 600, 500);
            
            // Configure stage
            primaryStage.setTitle("Budget Manager - Working!");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
            
            System.out.println("🎉 UI is now visible!");
            
        } catch (Exception e) {
            System.err.println("❌ Error starting application: " + e.getMessage());
            e.printStackTrace();
            
            // Show error in simple dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to start Budget Manager");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        DatabaseManager.closeConnection();
        System.out.println("👋 Application closed gracefully");
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Minimal Budget Manager...");
        launch(args);
    }
}
