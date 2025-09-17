package com.budget.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Income Management view
 */
public class IncomeController implements Initializable {
    
    @FXML private Button addIncomeButton;
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private Button clearFiltersButton;
    @FXML private Label totalIncomeLabel;
    @FXML private TableView<Object> incomeTable;
    @FXML private TableColumn<Object, String> incomeDateColumn;
    @FXML private TableColumn<Object, String> incomeCategoryColumn;
    @FXML private TableColumn<Object, String> incomeDescriptionColumn;
    @FXML private TableColumn<Object, String> incomeAmountColumn;
    @FXML private TableColumn<Object, String> incomeActionsColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        // TODO: Load income entries
    }
    
    private void setupFilters() {
        categoryFilterComboBox.getItems().add("All Categories");
        categoryFilterComboBox.setValue("All Categories");
        // TODO: Load categories from database
    }
    
    @FXML
    private void showAddIncomeDialog() {
        // TODO: Show add income dialog
        System.out.println("Add Income clicked");
    }
    
    @FXML
    private void applyFilters() {
        // TODO: Apply filters to income table
        System.out.println("Apply Filters clicked");
    }
    
    @FXML
    private void clearFilters() {
        // TODO: Clear all filters
        System.out.println("Clear Filters clicked");
    }
}
