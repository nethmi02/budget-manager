package com.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense model class representing an expense transaction.
 
 */
public class Expense {
    
    private int id;
    private BigDecimal amount;
    private String description;
    private int categoryId;
    private Category category; // Navigation property
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Expense() {
        this.createdAt = LocalDateTime.now();
        this.expenseDate = LocalDate.now();
    }
    
    // Constructor for new expenses
    public Expense(BigDecimal amount, String description, int categoryId, LocalDate expenseDate) {
        this();
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.expenseDate = expenseDate;
    }
    
    // Full constructor for database loading
    public Expense(int id, BigDecimal amount, String description, int categoryId, 
                   LocalDate expenseDate, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.expenseDate = expenseDate;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters with validation
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        if (description != null && !description.trim().isEmpty()) {
            this.description = description.trim();
        } else {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }
    
    public LocalDate getExpenseDate() {
        return expenseDate;
    }
    
    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Utility method to get formatted amount
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", description, getFormattedAmount(), expenseDate);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Expense expense = (Expense) obj;
        return id == expense.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
