package com.budget;

import com.budget.database.DatabaseManager;
import com.budget.dao.ExpenseDAO;
import com.budget.dao.IncomeDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Simple Budget Manager Application without FXML for testing
 */
public class SimpleBudgetApp extends Application {
    
    private ExpenseDAO expenseDAO;
    private IncomeDAO incomeDAO;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize database
        DatabaseManager.initializeDatabase();
        expenseDAO = new ExpenseDAO();
        incomeDAO = new IncomeDAO();
        
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Main content area with tabs
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);
        
        // Status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        // Create scene
        Scene scene = new Scene(root, 1200, 800);
        
        // Configure stage
        primaryStage.setTitle("Budget Manager - Simple Version");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // Handle application close
        primaryStage.setOnCloseRequest(event -> {
            DatabaseManager.closeConnection();
            System.exit(0);
        });
        
        primaryStage.show();
        
        System.out.println("Simple Budget Manager Application started successfully!");
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        
        HBox titleBox = new HBox();
        titleBox.setSpacing(20);
        
        Label title = new Label("Budget Manager");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label dateLabel = new Label("Today: " + LocalDate.now().toString());
        dateLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #6c757d;");
        
        titleBox.getChildren().addAll(title, spacer, dateLabel);
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Dashboard tab
        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setContent(createDashboard());
        
        // Expenses tab
        Tab expensesTab = new Tab("Expenses");
        expensesTab.setContent(createExpensesView());
        
        // Income tab
        Tab incomeTab = new Tab("Income");
        incomeTab.setContent(createIncomeView());
        
        // Budgets tab
        Tab budgetsTab = new Tab("Budgets");
        budgetsTab.setContent(createBudgetsView());
        
        tabPane.getTabs().addAll(dashboardTab, expensesTab, incomeTab, budgetsTab);
        
        return tabPane;
    }
    
    private VBox createDashboard() {
        VBox dashboard = new VBox();
        dashboard.setPadding(new Insets(20));
        dashboard.setSpacing(20);
        
        Label title = new Label("Financial Dashboard");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        // Summary cards
        HBox summaryCards = new HBox();
        summaryCards.setSpacing(20);
        
        VBox incomeCard = createSummaryCard("Total Income", "$0.00", "#27ae60");
        VBox expenseCard = createSummaryCard("Total Expenses", "$0.00", "#e74c3c");
        VBox netCard = createSummaryCard("Net Income", "$0.00", "#3498db");
        VBox budgetCard = createSummaryCard("Active Budgets", "0", "#f39c12");
        
        summaryCards.getChildren().addAll(incomeCard, expenseCard, netCard, budgetCard);
        
        // Recent transactions placeholder
        Label transactionsTitle = new Label("Recent Transactions");
        transactionsTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        TableView<String> transactionsTable = new TableView<>();
        TableColumn<String, String> dateCol = new TableColumn<>("Date");
        TableColumn<String, String> typeCol = new TableColumn<>("Type");
        TableColumn<String, String> categoryCol = new TableColumn<>("Category");
        TableColumn<String, String> descCol = new TableColumn<>("Description");
        TableColumn<String, String> amountCol = new TableColumn<>("Amount");
        
        transactionsTable.getColumns().addAll(dateCol, typeCol, categoryCol, descCol, amountCol);
        
        dashboard.getChildren().addAll(title, summaryCards, transactionsTitle, transactionsTable);
        VBox.setVgrow(transactionsTable, Priority.ALWAYS);
        
        return dashboard;
    }
    
    private VBox createSummaryCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 1; " +
                     "-fx-border-radius: 8; -fx-background-radius: 8;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #6c757d;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        
        return card;
    }
    
    private VBox createExpensesView() {
        VBox expensesView = new VBox();
        expensesView.setPadding(new Insets(20));
        expensesView.setSpacing(20);
        
        Label title = new Label("Expense Management");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Button addButton = new Button("Add Expense");
        addButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10;");
        addButton.setOnAction(e -> showAddExpenseDialog());
        
        TableView<String> expensesTable = new TableView<>();
        TableColumn<String, String> dateCol = new TableColumn<>("Date");
        TableColumn<String, String> categoryCol = new TableColumn<>("Category");
        TableColumn<String, String> descCol = new TableColumn<>("Description");
        TableColumn<String, String> amountCol = new TableColumn<>("Amount");
        
        expensesTable.getColumns().addAll(dateCol, categoryCol, descCol, amountCol);
        
        expensesView.getChildren().addAll(title, addButton, expensesTable);
        VBox.setVgrow(expensesTable, Priority.ALWAYS);
        
        return expensesView;
    }
    
    private VBox createIncomeView() {
        VBox incomeView = new VBox();
        incomeView.setPadding(new Insets(20));
        incomeView.setSpacing(20);
        
        Label title = new Label("Income Management");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Button addButton = new Button("Add Income");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10;");
        addButton.setOnAction(e -> showAddIncomeDialog());
        
        TableView<String> incomeTable = new TableView<>();
        TableColumn<String, String> dateCol = new TableColumn<>("Date");
        TableColumn<String, String> categoryCol = new TableColumn<>("Category");
        TableColumn<String, String> descCol = new TableColumn<>("Description");
        TableColumn<String, String> amountCol = new TableColumn<>("Amount");
        
        incomeTable.getColumns().addAll(dateCol, categoryCol, descCol, amountCol);
        
        incomeView.getChildren().addAll(title, addButton, incomeTable);
        VBox.setVgrow(incomeTable, Priority.ALWAYS);
        
        return incomeView;
    }
    
    private VBox createBudgetsView() {
        VBox budgetsView = new VBox();
        budgetsView.setPadding(new Insets(20));
        budgetsView.setSpacing(20);
        
        Label title = new Label("Budget Management");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Button addButton = new Button("Create Budget");
        addButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 10;");
        addButton.setOnAction(e -> showAddBudgetDialog());
        
        TableView<String> budgetsTable = new TableView<>();
        TableColumn<String, String> categoryCol = new TableColumn<>("Category");
        TableColumn<String, String> budgetCol = new TableColumn<>("Budget");
        TableColumn<String, String> spentCol = new TableColumn<>("Spent");
        TableColumn<String, String> remainingCol = new TableColumn<>("Remaining");
        
        budgetsTable.getColumns().addAll(categoryCol, budgetCol, spentCol, remainingCol);
        
        budgetsView.getChildren().addAll(title, addButton, budgetsTable);
        VBox.setVgrow(budgetsTable, Priority.ALWAYS);
        
        return budgetsView;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 20, 5, 20));
        statusBar.setSpacing(20);
        statusBar.setStyle("-fx-background-color: #343a40;");
        
        Label statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label incomeLabel = new Label("Total Income: $0.00");
        incomeLabel.setStyle("-fx-text-fill: white;");
        
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: white;");
        
        Label expenseLabel = new Label("Total Expenses: $0.00");
        expenseLabel.setStyle("-fx-text-fill: white;");
        
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: white;");
        
        Label netLabel = new Label("Net: $0.00");
        netLabel.setStyle("-fx-text-fill: white;");
        
        statusBar.getChildren().addAll(statusLabel, spacer, incomeLabel, sep1, expenseLabel, sep2, netLabel);
        
        return statusBar;
    }
    
    private void showAddExpenseDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Expense");
        alert.setHeaderText("Add New Expense");
        alert.setContentText("Add expense functionality will be implemented here.");
        alert.showAndWait();
    }
    
    private void showAddIncomeDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Income");
        alert.setHeaderText("Add New Income");
        alert.setContentText("Add income functionality will be implemented here.");
        alert.showAndWait();
    }
    
    private void showAddBudgetDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Budget");
        alert.setHeaderText("Create New Budget");
        alert.setContentText("Create budget functionality will be implemented here.");
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
