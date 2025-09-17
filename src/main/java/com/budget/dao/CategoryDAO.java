package com.budget.dao;

import com.budget.database.DatabaseManager;
import com.budget.model.Category;
import com.budget.model.Category.CategoryType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO handles all database operations for Category objects.
 */
public class CategoryDAO {
    
    private final DatabaseManager dbManager;
    
    public CategoryDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create a new category in the database
     * @param category The category to save
     * @return The category with updated ID, or null if failed
     */
    public Category create(Category category) {
        String sql = "INSERT INTO categories (name, type, color) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set parameters (? placeholders)
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());
            stmt.setString(3, category.getColor());
            
            // Execute the insert
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                        return category;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating category: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find a category by ID
     * @param id The category ID
     * @return The category, or null if not found
     */
    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding category by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all categories
     * @return List of all categories
     */
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY type, name";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get categories by type (EXPENSE or INCOME)
     * @param type The category type
     * @return List of categories of the specified type
     */
    public List<Category> findByType(CategoryType type) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? ORDER BY name";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding categories by type: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Update an existing category
     * @param category The category to update
     * @return true if successful, false otherwise
     */
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, type = ?, color = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());
            stmt.setString(3, category.getColor());
            stmt.setInt(4, category.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a category by ID
     * @param id The category ID to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if a category name already exists
     * @param name The category name to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if name exists, false otherwise
     */
    public boolean nameExists(String name, int excludeId) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ? AND id != ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking category name: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Helper method to convert ResultSet to Category object
     * This demonstrates the mapping between database rows and Java objects
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        CategoryType type = CategoryType.valueOf(rs.getString("type"));
        String color = rs.getString("color");
        
        // Handle timestamp conversion
        Timestamp timestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
        
        return new Category(id, name, type, color, createdAt);
    }
}
