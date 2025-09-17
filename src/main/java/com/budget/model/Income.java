package com.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Income model class representing user income
 */
public class Income {
    private int id;
    private int categoryId;
    private Category category; 
    private BigDecimal amount;
    private String description;
    private LocalDate incomeDate;
    private LocalDateTime createdDate;
    
    // Constructors
    public Income() {}
    
    public Income(int categoryId, BigDecimal amount, String description, LocalDate incomeDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.incomeDate = incomeDate;
        this.createdDate = LocalDateTime.now();
    }
    
    public Income(int id, int categoryId, BigDecimal amount, String description, 
                  LocalDate incomeDate, LocalDateTime createdDate) {
        this.id = id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.incomeDate = incomeDate;
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
    
    public LocalDate getIncomeDate() {
        return incomeDate;
    }
    
    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
        return String.format("Income{id=%d, category='%s', amount=%.2f, description='%s', date=%s}",
                id, category != null ? category.getName() : "Unknown", 
                amount != null ? amount.doubleValue() : 0.0, description, incomeDate);
    }
}
