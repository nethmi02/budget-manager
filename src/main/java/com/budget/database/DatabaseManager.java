package com.budget.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager handles SQLite database connection and initialization.
 
 */
public class DatabaseManager {
    
    // Singleton instance - only one DatabaseManager exists
    private static DatabaseManager instance;
    private Connection connection;
    
    // Database file will be created in the project directory
    private static final String DATABASE_URL = "jdbc:sqlite:budget_manager.db";
    
    // Private constructor prevents creating multiple instances
    private DatabaseManager() {
        initializeDatabase();
    }
    
    /**
     * Get the single instance of DatabaseManager (Singleton pattern)
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get the database connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is still valid
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
        } catch (SQLException e) {
            System.err.println("Error getting database connection: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Initialize the database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            createTables();
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    /**
     * Create all necessary tables for our budget application
     */
    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        
        // Categories table - stores expense/income categories
        String createCategoriesTable = """
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name VARCHAR(100) NOT NULL UNIQUE,
                type VARCHAR(20) NOT NULL CHECK (type IN ('EXPENSE', 'INCOME')),
                color VARCHAR(7) DEFAULT '#3498db',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Expenses table - stores all expense records
        String createExpensesTable = """
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
                description VARCHAR(255) NOT NULL,
                category_id INTEGER NOT NULL,
                expense_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(id)
            )
        """;
        
        // Income table - stores all income records
        String createIncomeTable = """
            CREATE TABLE IF NOT EXISTS income (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
                description VARCHAR(255) NOT NULL,
                category_id INTEGER NOT NULL,
                income_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(id)
            )
        """;
        
        // Budgets table - stores budget limits for categories
        String createBudgetsTable = """
            CREATE TABLE IF NOT EXISTS budgets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category_id INTEGER NOT NULL,
                amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
                period VARCHAR(20) NOT NULL CHECK (period IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
                start_date DATE NOT NULL,
                end_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(id),
                UNIQUE(category_id, period, start_date)
            )
        """;
        
        // Execute all table creation statements
        statement.execute(createCategoriesTable);
        statement.execute(createExpensesTable);
        statement.execute(createIncomeTable);
        statement.execute(createBudgetsTable);
        
        // Insert default categories if they don't exist
        insertDefaultCategories();
        
        statement.close();
    }
    
    /**
     * Insert some default categories to get started
     */
    private void insertDefaultCategories() throws SQLException {
        String insertCategories = """
            INSERT OR IGNORE INTO categories (name, type, color) VALUES
            ('Food & Dining', 'EXPENSE', '#e74c3c'),
            ('Transportation', 'EXPENSE', '#f39c12'),
            ('Shopping', 'EXPENSE', '#9b59b6'),
            ('Entertainment', 'EXPENSE', '#3498db'),
            ('Utilities', 'EXPENSE', '#2ecc71'),
            ('Healthcare', 'EXPENSE', '#e67e22'),
            ('Salary', 'INCOME', '#27ae60'),
            ('Freelance', 'INCOME', '#16a085'),
            ('Investments', 'INCOME', '#8e44ad')
        """;
        
        Statement statement = connection.createStatement();
        statement.execute(insertCategories);
        statement.close();
    }
    
    /**
     * Close the database connection when application shuts down
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
