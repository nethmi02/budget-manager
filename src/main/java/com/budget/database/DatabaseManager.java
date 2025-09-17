package com.budget.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Manager class to handle SQLite database connections and schema creation
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:budget.db";
    private static Connection connection;
    
    /**
     * Get database connection (singleton pattern)
     * @return Connection object
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }
    
    /**
     * Initialize database schema - create all required tables
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Categories table
            String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    type TEXT NOT NULL CHECK(type IN ('EXPENSE', 'INCOME')),
                    color TEXT DEFAULT '#3498db',
                    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // Create Budgets table
            String createBudgetsTable = """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category_id INTEGER,
                    amount DECIMAL(10,2) NOT NULL,
                    period TEXT NOT NULL CHECK(period IN ('MONTHLY', 'WEEKLY', 'YEARLY')),
                    start_date DATE NOT NULL,
                    end_date DATE,
                    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
                )
            """;
            
            // Create Expenses table
            String createExpensesTable = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category_id INTEGER NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    description TEXT,
                    expense_date DATE NOT NULL,
                    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
                )
            """;
            
            // Create Income table
            String createIncomeTable = """
                CREATE TABLE IF NOT EXISTS income (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category_id INTEGER NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    description TEXT,
                    income_date DATE NOT NULL,
                    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
                )
            """;
            
            // Execute all table creation statements
            stmt.execute(createCategoriesTable);
            stmt.execute(createBudgetsTable);
            stmt.execute(createExpensesTable);
            stmt.execute(createIncomeTable);
            
            // Insert default categories if they don't exist
            insertDefaultCategories(stmt);
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Insert default categories for expenses and income
     */
    private static void insertDefaultCategories(Statement stmt) throws SQLException {
        // Default expense categories
        String[] expenseCategories = {
            "Food & Dining", "Transportation", "Shopping", "Entertainment",
            "Bills & Utilities", "Healthcare", "Education", "Travel"
        };
        
        // Default income categories
        String[] incomeCategories = {
            "Salary", "Freelance", "Investment", "Business", "Other Income"
        };
        
        // Insert expense categories
        for (String category : expenseCategories) {
            String insertExpenseCategory = String.format(
                "INSERT OR IGNORE INTO categories (name, type) VALUES ('%s', 'EXPENSE')",
                category
            );
            stmt.execute(insertExpenseCategory);
        }
        
        // Insert income categories
        for (String category : incomeCategories) {
            String insertIncomeCategory = String.format(
                "INSERT OR IGNORE INTO categories (name, type) VALUES ('%s', 'INCOME')",
                category
            );
            stmt.execute(insertIncomeCategory);
        }
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
