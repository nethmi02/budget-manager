package com.budget.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Dashboard view
 */
public class DashboardController implements Initializable {
    
    @FXML private ComboBox<String> periodComboBox;
    @FXML private Label totalIncomeValue;
    @FXML private Label totalExpensesValue;
    @FXML private Label netIncomeValue;
    @FXML private Label activeBudgetsValue;
    @FXML private TableView<Object> recentTransactionsTable;
    @FXML private TableColumn<Object, String> dateColumn;
    @FXML private TableColumn<Object, String> typeColumn;
    @FXML private TableColumn<Object, String> categoryColumn;
    @FXML private TableColumn<Object, String> descriptionColumn;
    @FXML private TableColumn<Object, String> amountColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPeriodComboBox();
        // TODO: Load dashboard data
    }
    
    private void setupPeriodComboBox() {
        periodComboBox.getItems().addAll(
            "This Month", "Last Month", "This Year", "Last Year", "All Time"
        );
        periodComboBox.setValue("This Month");
    }
}
