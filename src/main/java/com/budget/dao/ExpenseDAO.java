package com.budget.dao;

import com.budget.database.DatabaseManager;
import com.budget.model.Category;
import com.budget.model.Expense;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Expense operations
 */
public class ExpenseDAO {
    private final CategoryDAO categoryDAO;
    
    public ExpenseDAO() {
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Create a new expense
     */
    public boolean create(Expense expense) {
        String sql = "INSERT INTO expenses (category_id, amount, description, expense_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, expense.getCategoryId());
            pstmt.setBigDecimal(2, expense.getAmount());
            pstmt.setString(3, expense.getDescription());
            pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        expense.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating expense: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Find expense by ID
     */
    public Optional<Expense> findById(int id) {
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            LEFT JOIN categories c ON e.category_id = c.id
            WHERE e.id = ?
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractExpenseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding expense by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all expenses
     */
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            LEFT JOIN categories c ON e.category_id = c.id
            ORDER BY e.expense_date DESC, e.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                expenses.add(extractExpenseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all expenses: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Find expenses by date range
     */
    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            LEFT JOIN categories c ON e.category_id = c.id
            WHERE e.expense_date BETWEEN ? AND ?
            ORDER BY e.expense_date DESC, e.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                expenses.add(extractExpenseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding expenses by date range: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Find expenses by category
     */
    public List<Expense> findByCategory(int categoryId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM expenses e
            LEFT JOIN categories c ON e.category_id = c.id
            WHERE e.category_id = ?
            ORDER BY e.expense_date DESC, e.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                expenses.add(extractExpenseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding expenses by category: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get total expenses for a category in date range
     */
    public BigDecimal getTotalByCategoryAndDateRange(int categoryId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE category_id = ? AND expense_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total expenses by category and date range: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Get total expenses for date range
     */
    public BigDecimal getTotalByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE expense_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total expenses by date range: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Update existing expense
     */
    public boolean update(Expense expense) {
        String sql = "UPDATE expenses SET category_id = ?, amount = ?, description = ?, expense_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, expense.getCategoryId());
            pstmt.setBigDecimal(2, expense.getAmount());
            pstmt.setString(3, expense.getDescription());
            pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            pstmt.setInt(5, expense.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete expense by ID
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extract Expense object from ResultSet
     */
    private Expense extractExpenseFromResultSet(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setCategoryId(rs.getInt("category_id"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setDescription(rs.getString("description"));
        
        Date expenseDate = rs.getDate("expense_date");
        if (expenseDate != null) {
            expense.setExpenseDate(expenseDate.toLocalDate());
        }
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            expense.setCreatedDate(createdTimestamp.toLocalDateTime());
        }
        
        // Set category if available
        String categoryName = rs.getString("category_name");
        if (categoryName != null) {
            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(categoryName);
            category.setType(Category.CategoryType.valueOf(rs.getString("category_type")));
            category.setColor(rs.getString("category_color"));
            expense.setCategory(category);
        }
        
        return expense;
    }
}
