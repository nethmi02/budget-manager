package com.budget.dao;

import com.budget.database.DatabaseManager;
import com.budget.model.Category;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Category operations
 */
public class CategoryDAO {
    
    /**
     * Create a new category
     */
    public boolean create(Category category) {
        String sql = "INSERT INTO categories (name, type, color) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType().toString());
            pstmt.setString(3, category.getColor());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating category: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Find category by ID
     */
    public Optional<Category> findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractCategoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding category by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all categories
     */
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY type, name";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Find categories by type
     */
    public List<Category> findByType(Category.CategoryType type) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? ORDER BY name";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding categories by type: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Update existing category
     */
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, type = ?, color = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType().toString());
            pstmt.setString(3, category.getColor());
            pstmt.setInt(4, category.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete category by ID
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if category name exists (for validation)
     */
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking category name existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extract Category object from ResultSet
     */
    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setType(Category.CategoryType.valueOf(rs.getString("type")));
        category.setColor(rs.getString("color"));
        
        Timestamp timestamp = rs.getTimestamp("created_date");
        if (timestamp != null) {
            category.setCreatedDate(timestamp.toLocalDateTime());
        }
        
        return category;
    }
}
