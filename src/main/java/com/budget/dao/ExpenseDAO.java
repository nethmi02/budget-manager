package com.budget.dao;

import com.budget.database.DatabaseManager;
import com.budget.model.Category;
import com.budget.model.Expense;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExpenseDAO handles all database operations for Expense objects.
 * Includes advanced queries for financial analysis.
 */
public class ExpenseDAO {
    
    private final DatabaseManager dbManager;
    private final CategoryDAO categoryDAO;
    
    public ExpenseDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Create a new expense
     */
    public Expense create(Expense expense) {
        String sql = "INSERT INTO expenses (amount, description, category_id, expense_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setString(2, expense.getDescription());
            stmt.setInt(3, expense.getCategoryId());
            stmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        expense.setId(generatedKeys.getInt(1));
                        return expense;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating expense: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find expense by ID with category information
     */
    public Expense findById(int id) {
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpense(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding expense by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all expenses with category information
     */
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            ORDER BY e.expense_date DESC, e.created_at DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all expenses: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get expenses within a date range
     */
    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.expense_date BETWEEN ? AND ?
            ORDER BY e.expense_date DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding expenses by date range: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get expenses by category
     */
    public List<Expense> findByCategory(int categoryId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.category_id = ?
            ORDER BY e.expense_date DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding expenses by category: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get total expenses by category for chart data
     * Returns a map of category name to total amount
     */
    public Map<String, BigDecimal> getTotalsByCategory(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> totals = new HashMap<>();
        String sql = """
            SELECT c.name, SUM(e.amount) as total
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.expense_date BETWEEN ? AND ?
            GROUP BY c.id, c.name
            ORDER BY total DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    totals.put(rs.getString("name"), rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting expense totals by category: " + e.getMessage());
        }
        
        return totals;
    }
    
    /**
     * Get total expenses for a date range
     */
    public BigDecimal getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM expenses WHERE expense_date BETWEEN ? AND ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total expenses: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Update an expense
     */
    public boolean update(Expense expense) {
        String sql = "UPDATE expenses SET amount = ?, description = ?, category_id = ?, expense_date = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setString(2, expense.getDescription());
            stmt.setInt(3, expense.getCategoryId());
            stmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            stmt.setInt(5, expense.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an expense
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper method to map ResultSet to Expense with Category
     */
    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        BigDecimal amount = rs.getBigDecimal("amount");
        String description = rs.getString("description");
        int categoryId = rs.getInt("category_id");
        LocalDate expenseDate = rs.getDate("expense_date").toLocalDate();
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
        
        Expense expense = new Expense(id, amount, description, categoryId, expenseDate, createdAt);
        
        // Set category information if available in the ResultSet
        try {
            String categoryName = rs.getString("category_name");
            if (categoryName != null) {
                Category.CategoryType categoryType = Category.CategoryType.valueOf(rs.getString("category_type"));
                String categoryColor = rs.getString("color");
                Category category = new Category(categoryId, categoryName, categoryType, categoryColor, null);
                expense.setCategory(category);
            }
        } catch (SQLException e) {
            // Category information not in this query, that's okay
        }
        
        return expense;
    }
}
