package com.budget.model;

import java.time.LocalDateTime;

/**
 * Category model class representing expense and income categories
 */
public class Category {
    public enum CategoryType {
        EXPENSE, INCOME
    }
    
    private int id;
    private String name;
    private CategoryType type;
    private String color;
    private LocalDateTime createdDate;
    
    // Constructors
    public Category() {}
    
    public Category(String name, CategoryType type) {
        this.name = name;
        this.type = type;
        this.color = "#3498db"; 
        this.createdDate = LocalDateTime.now();
    }
    
    public Category(int id, String name, CategoryType type, String color, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.createdDate = createdDate;
    }
    
    // Getters and Setters
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
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
        return name; // For ComboBox display
    }
    
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
