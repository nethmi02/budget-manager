package com.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense model class representing user expenses
 */
public class Expense {
    private int id;
    private int categoryId;
    private Category category; 
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdDate;
    
    // Constructors
    public Expense() {}
    
    public Expense(int categoryId, BigDecimal amount, String description, LocalDate expenseDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.createdDate = LocalDateTime.now();
    }
    
    public Expense(int id, int categoryId, BigDecimal amount, String description, 
                   LocalDate expenseDate, LocalDateTime createdDate) {
        this.id = id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.createdDate = createdDate;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getExpenseDate() {
        return expenseDate;
    }
    
    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
        return String.format("Expense{id=%d, category='%s', amount=%.2f, description='%s', date=%s}",
                id, category != null ? category.getName() : "Unknown", 
                amount != null ? amount.doubleValue() : 0.0, description, expenseDate);
    }
}
