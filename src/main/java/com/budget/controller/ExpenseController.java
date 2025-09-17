package com.budget.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Expense Management view
 */
public class ExpenseController implements Initializable {
    
    @FXML private Button addExpenseButton;
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private Button clearFiltersButton;
    @FXML private Label totalExpensesLabel;
    @FXML private TableView<Object> expensesTable;
    @FXML private TableColumn<Object, String> expenseDateColumn;
    @FXML private TableColumn<Object, String> expenseCategoryColumn;
    @FXML private TableColumn<Object, String> expenseDescriptionColumn;
    @FXML private TableColumn<Object, String> expenseAmountColumn;
    @FXML private TableColumn<Object, String> expenseActionsColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        // TODO: Load expenses
    }
    
    private void setupFilters() {
        categoryFilterComboBox.getItems().add("All Categories");
        categoryFilterComboBox.setValue("All Categories");
        // TODO: Load categories from database
    }
    
    @FXML
    private void showAddExpenseDialog() {
        // TODO: Show add expense dialog
        System.out.println("Add Expense clicked");
    }
    
    @FXML
    private void applyFilters() {
        // TODO: Apply filters to expense table
        System.out.println("Apply Filters clicked");
    }
    
    @FXML
    private void clearFilters() {
        // TODO: Clear all filters
        System.out.println("Clear Filters clicked");
    }
}
