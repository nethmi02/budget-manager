package com.budget.model;

import java.time.LocalDateTime;

/**
 * Category model class representing expense and income categories.
 
 */
public class Category {
    
    // Private fields (Encapsulation principle)
    private int id;
    private String name;
    private CategoryType type;
    private String color;
    private LocalDateTime createdAt;
    
    // Enum for category types (better than using strings)
    public enum CategoryType {
        EXPENSE, INCOME
    }
    
    // Default constructor
    public Category() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor without ID (for new categories)
    public Category(String name, CategoryType type, String color) {
        this();
        this.name = name;
        this.type = type;
        this.color = color;
    }
    
    // Full constructor (for categories loaded from database)
    public Category(int id, String name, CategoryType type, String color, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CategoryType getType() {
        return type;
    }
    
    public void setType(CategoryType type) {
        this.type = type;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // toString method for debugging and display
    @Override
    public String toString() {
        return name; // This will be used in ComboBoxes
    }
    
    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return id == category.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
