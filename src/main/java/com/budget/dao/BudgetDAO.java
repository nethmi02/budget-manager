package com.budget.dao;

import com.budget.database.DatabaseManager;
import com.budget.model.Budget;
import com.budget.model.Category;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Budget operations
 */
public class BudgetDAO {
    private final CategoryDAO categoryDAO;
    
    public BudgetDAO() {
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Create a new budget
     */
    public boolean create(Budget budget) {
        String sql = "INSERT INTO budgets (category_id, amount, period, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, budget.getCategoryId());
            pstmt.setBigDecimal(2, budget.getAmount());
            pstmt.setString(3, budget.getPeriod().toString());
            pstmt.setDate(4, Date.valueOf(budget.getStartDate()));
            pstmt.setDate(5, budget.getEndDate() != null ? Date.valueOf(budget.getEndDate()) : null);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        budget.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating budget: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Find budget by ID
     */
    public Optional<Budget> findById(int id) {
        String sql = """
            SELECT b.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM budgets b
            LEFT JOIN categories c ON b.category_id = c.id
            WHERE b.id = ?
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractBudgetFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding budget by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all budgets
     */
    public List<Budget> findAll() {
        List<Budget> budgets = new ArrayList<>();
        String sql = """
            SELECT b.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM budgets b
            LEFT JOIN categories c ON b.category_id = c.id
            ORDER BY b.start_date DESC, b.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                budgets.add(extractBudgetFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all budgets: " + e.getMessage());
        }
        
        return budgets;
    }
    
    /**
     * Find active budgets (current date within start and end date)
     */
    public List<Budget> findActiveBudgets() {
        List<Budget> budgets = new ArrayList<>();
        String sql = """
            SELECT b.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM budgets b
            LEFT JOIN categories c ON b.category_id = c.id
            WHERE ? BETWEEN b.start_date AND b.end_date
            ORDER BY b.start_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(extractBudgetFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding active budgets: " + e.getMessage());
        }
        
        return budgets;
    }
    
    /**
     * Find budgets by category
     */
    public List<Budget> findByCategory(int categoryId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = """
            SELECT b.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM budgets b
            LEFT JOIN categories c ON b.category_id = c.id
            WHERE b.category_id = ?
            ORDER BY b.start_date DESC, b.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(extractBudgetFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding budgets by category: " + e.getMessage());
        }
        
        return budgets;
    }
    
    /**
     * Find active budget for a specific category
     */
    public Optional<Budget> findActiveBudgetByCategory(int categoryId) {
        String sql = """
            SELECT b.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM budgets b
            LEFT JOIN categories c ON b.category_id = c.id
            WHERE b.category_id = ? AND ? BETWEEN b.start_date AND b.end_date
            ORDER BY b.start_date DESC
            LIMIT 1
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            pstmt.setDate(2, Date.valueOf(LocalDate.now()));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractBudgetFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding active budget by category: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find budgets that overlap with given date range
     */
    public List<Budget> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Budget> budgets = new ArrayList<>();
        String sql = """
            SELECT b.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM budgets b
            LEFT JOIN categories c ON b.category_id = c.id
            WHERE (b.start_date <= ? AND b.end_date >= ?) OR
                  (b.start_date <= ? AND b.end_date >= ?) OR
                  (b.start_date >= ? AND b.end_date <= ?)
            ORDER BY b.start_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(endDate));
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(startDate));
            pstmt.setDate(5, Date.valueOf(startDate));
            pstmt.setDate(6, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(extractBudgetFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding budgets by date range: " + e.getMessage());
        }
        
        return budgets;
    }
    
    /**
     * Update existing budget
     */
    public boolean update(Budget budget) {
        String sql = "UPDATE budgets SET category_id = ?, amount = ?, period = ?, start_date = ?, end_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, budget.getCategoryId());
            pstmt.setBigDecimal(2, budget.getAmount());
            pstmt.setString(3, budget.getPeriod().toString());
            pstmt.setDate(4, Date.valueOf(budget.getStartDate()));
            pstmt.setDate(5, budget.getEndDate() != null ? Date.valueOf(budget.getEndDate()) : null);
            pstmt.setInt(6, budget.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating budget: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete budget by ID
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting budget: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if category has active budget
     */
    public boolean hasActiveBudget(int categoryId) {
        String sql = "SELECT COUNT(*) FROM budgets WHERE category_id = ? AND ? BETWEEN start_date AND end_date";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            pstmt.setDate(2, Date.valueOf(LocalDate.now()));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking active budget: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extract Budget object from ResultSet
     */
    private Budget extractBudgetFromResultSet(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getInt("id"));
        budget.setCategoryId(rs.getInt("category_id"));
        budget.setAmount(rs.getBigDecimal("amount"));
        budget.setPeriod(Budget.Period.valueOf(rs.getString("period")));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            budget.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            budget.setEndDate(endDate.toLocalDate());
        }
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            budget.setCreatedDate(createdTimestamp.toLocalDateTime());
        }
        
        // Set category if available
        String categoryName = rs.getString("category_name");
        if (categoryName != null) {
            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(categoryName);
            category.setType(Category.CategoryType.valueOf(rs.getString("category_type")));
            category.setColor(rs.getString("category_color"));
            budget.setCategory(category);
        }
        
        return budget;
    }
}
