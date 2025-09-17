package com.budget.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Budget Management view
 */
public class BudgetController implements Initializable {
    
    @FXML private Button addBudgetButton;
    @FXML private TableView<Object> activeBudgetsTable;
    @FXML private TableColumn<Object, String> budgetCategoryColumn;
    @FXML private TableColumn<Object, String> budgetAmountColumn;
    @FXML private TableColumn<Object, String> budgetSpentColumn;
    @FXML private TableColumn<Object, String> budgetRemainingColumn;
    @FXML private TableColumn<Object, String> budgetProgressColumn;
    @FXML private TableColumn<Object, String> budgetPeriodColumn;
    @FXML private TableColumn<Object, String> budgetActionsColumn;
    
    @FXML private TableView<Object> allBudgetsTable;
    @FXML private TableColumn<Object, String> allBudgetCategoryColumn;
    @FXML private TableColumn<Object, String> allBudgetAmountColumn;
    @FXML private TableColumn<Object, String> allBudgetPeriodColumn;
    @FXML private TableColumn<Object, String> allBudgetStartDateColumn;
    @FXML private TableColumn<Object, String> allBudgetEndDateColumn;
    @FXML private TableColumn<Object, String> allBudgetStatusColumn;
    @FXML private TableColumn<Object, String> allBudgetActionsColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load budgets
    }
    
    @FXML
    private void showAddBudgetDialog() {
        // TODO: Show add budget dialog
        System.out.println("Create Budget clicked");
    }
}
