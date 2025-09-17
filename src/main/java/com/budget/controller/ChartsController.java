package com.budget.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Charts & Analytics view
 */
public class ChartsController implements Initializable {
    
    @FXML private ComboBox<String> chartPeriodComboBox;
    @FXML private TabPane chartsTabPane;
    @FXML private StackPane expenseChartContainer;
    @FXML private StackPane incomeChartContainer;
    @FXML private StackPane budgetChartContainer;
    @FXML private StackPane trendsChartContainer;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPeriodComboBox();
        // TODO: Load charts
    }
    
    private void setupPeriodComboBox() {
        chartPeriodComboBox.getItems().addAll(
            "This Month", "Last Month", "This Quarter", "This Year", "Last Year"
        );
        chartPeriodComboBox.setValue("This Month");
    }
}