package com.budget.controller;

import com.budget.dao.ExpenseDAO;
import com.budget.dao.IncomeDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Main Controller for the Budget Manager application
 */
public class MainController implements Initializable {
    
    @FXML private TabPane mainTabPane;
    @FXML private Label currentDateLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpensesLabel;
    @FXML private Label netIncomeLabel;
    
    private final ExpenseDAO expenseDAO;
    private final IncomeDAO incomeDAO;
    
    public MainController() {
        this.expenseDAO = new ExpenseDAO();
        this.incomeDAO = new IncomeDAO();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadFinancialSummary();
    }
    
    /**
     * Setup the UI components
     */
    private void setupUI() {
        // Set current date
        currentDateLabel.setText("Today: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        
        // Set initial status
        updateStatus("Application ready");
    }
    
    /**
     * Load financial summary for status bar
     */
    private void loadFinancialSummary() {
        updateStatus("Loading financial summary...");
        
        CompletableFuture.supplyAsync(() -> {
            // Get current month's date range
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            
            // Calculate totals
            BigDecimal totalIncome = incomeDAO.getTotalByDateRange(startOfMonth, endOfMonth);
            BigDecimal totalExpenses = expenseDAO.getTotalByDateRange(startOfMonth, endOfMonth);
            BigDecimal netIncome = totalIncome.subtract(totalExpenses);
            
            return new FinancialSummary(totalIncome, totalExpenses, netIncome);
            
        }).thenAccept(summary -> {
            Platform.runLater(() -> {
                updateFinancialSummary(summary);
                updateStatus("Ready");
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                updateStatus("Error loading financial summary: " + throwable.getMessage());
            });
            return null;
        });
    }
    
    /**
     * Update financial summary in status bar
     */
    private void updateFinancialSummary(FinancialSummary summary) {
        totalIncomeLabel.setText(String.format("Total Income: $%.2f", summary.totalIncome.doubleValue()));
        totalExpensesLabel.setText(String.format("Total Expenses: $%.2f", summary.totalExpenses.doubleValue()));
        
        String netText = String.format("Net: $%.2f", summary.netIncome.doubleValue());
        netIncomeLabel.setText(netText);
        
        // Color code the net income
        if (summary.netIncome.compareTo(BigDecimal.ZERO) > 0) {
            netIncomeLabel.setStyle("-fx-text-fill: #27ae60;"); // Green for positive
        } else if (summary.netIncome.compareTo(BigDecimal.ZERO) < 0) {
            netIncomeLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red for negative
        } else {
            netIncomeLabel.setStyle("-fx-text-fill: #7f8c8d;"); // Gray for zero
        }
    }
    
    /**
     * Update status message
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Refresh financial summary (can be called from other controllers)
     */
    public void refreshFinancialSummary() {
        loadFinancialSummary();
    }
    
    /**
     * Get reference to main tab pane (for navigation from other controllers)
     */
    public TabPane getMainTabPane() {
        return mainTabPane;
    }
    
    /**
     * Inner class to hold financial summary data
     */
    private static class FinancialSummary {
        final BigDecimal totalIncome;
        final BigDecimal totalExpenses;
        final BigDecimal netIncome;
        
        FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netIncome) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netIncome = netIncome;
        }
    }
}
