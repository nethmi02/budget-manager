package com.budget.dao;

import com.budget.database.DatabaseManager;
import com.budget.model.Category;
import com.budget.model.Income;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Income operations
 */
public class IncomeDAO {
    private final CategoryDAO categoryDAO;
    
    public IncomeDAO() {
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Create a new income entry
     */
    public boolean create(Income income) {
        String sql = "INSERT INTO income (category_id, amount, description, income_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, income.getCategoryId());
            pstmt.setBigDecimal(2, income.getAmount());
            pstmt.setString(3, income.getDescription());
            pstmt.setDate(4, Date.valueOf(income.getIncomeDate()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        income.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating income: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Find income by ID
     */
    public Optional<Income> findById(int id) {
        String sql = """
            SELECT i.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM income i
            LEFT JOIN categories c ON i.category_id = c.id
            WHERE i.id = ?
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractIncomeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding income by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all income entries
     */
    public List<Income> findAll() {
        List<Income> incomes = new ArrayList<>();
        String sql = """
            SELECT i.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM income i
            LEFT JOIN categories c ON i.category_id = c.id
            ORDER BY i.income_date DESC, i.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                incomes.add(extractIncomeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all income: " + e.getMessage());
        }
        
        return incomes;
    }
    
    /**
     * Find income by date range
     */
    public List<Income> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Income> incomes = new ArrayList<>();
        String sql = """
            SELECT i.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM income i
            LEFT JOIN categories c ON i.category_id = c.id
            WHERE i.income_date BETWEEN ? AND ?
            ORDER BY i.income_date DESC, i.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                incomes.add(extractIncomeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding income by date range: " + e.getMessage());
        }
        
        return incomes;
    }
    
    /**
     * Find income by category
     */
    public List<Income> findByCategory(int categoryId) {
        List<Income> incomes = new ArrayList<>();
        String sql = """
            SELECT i.*, c.name as category_name, c.type as category_type, c.color as category_color
            FROM income i
            LEFT JOIN categories c ON i.category_id = c.id
            WHERE i.category_id = ?
            ORDER BY i.income_date DESC, i.created_date DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                incomes.add(extractIncomeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding income by category: " + e.getMessage());
        }
        
        return incomes;
    }
    
    /**
     * Get total income for a category in date range
     */
    public BigDecimal getTotalByCategoryAndDateRange(int categoryId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM income WHERE category_id = ? AND income_date BETWEEN ? AND ?";
        
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
            System.err.println("Error getting total income by category and date range: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Get total income for date range
     */
    public BigDecimal getTotalByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM income WHERE income_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total income by date range: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Update existing income
     */
    public boolean update(Income income) {
        String sql = "UPDATE income SET category_id = ?, amount = ?, description = ?, income_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, income.getCategoryId());
            pstmt.setBigDecimal(2, income.getAmount());
            pstmt.setString(3, income.getDescription());
            pstmt.setDate(4, Date.valueOf(income.getIncomeDate()));
            pstmt.setInt(5, income.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating income: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete income by ID
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM income WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting income: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extract Income object from ResultSet
     */
    private Income extractIncomeFromResultSet(ResultSet rs) throws SQLException {
        Income income = new Income();
        income.setId(rs.getInt("id"));
        income.setCategoryId(rs.getInt("category_id"));
        income.setAmount(rs.getBigDecimal("amount"));
        income.setDescription(rs.getString("description"));
        
        Date incomeDate = rs.getDate("income_date");
        if (incomeDate != null) {
            income.setIncomeDate(incomeDate.toLocalDate());
        }
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            income.setCreatedDate(createdTimestamp.toLocalDateTime());
        }
        
        // Set category if available
        String categoryName = rs.getString("category_name");
        if (categoryName != null) {
            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(categoryName);
            category.setType(Category.CategoryType.valueOf(rs.getString("category_type")));
            category.setColor(rs.getString("category_color"));
            income.setCategory(category);
        }
        
        return income;
    }
}
