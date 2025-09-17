package com.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Income model class representing an income transaction.
 * Similar to Expense but for money coming in.
 */
public class Income {
    
    private int id;
    private BigDecimal amount;
    private String description;
    private int categoryId;
    private Category category;
    private LocalDate incomeDate;
    private LocalDateTime createdAt;
    
    public Income() {
        this.createdAt = LocalDateTime.now();
        this.incomeDate = LocalDate.now();
    }
    
    public Income(BigDecimal amount, String description, int categoryId, LocalDate incomeDate) {
        this();
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.incomeDate = incomeDate;
    }
    
    public Income(int id, BigDecimal amount, String description, int categoryId, 
                  LocalDate incomeDate, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.incomeDate = incomeDate;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters (similar validation as Expense)
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
    
    public LocalDate getIncomeDate() {
        return incomeDate;
    }
    
    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", description, getFormattedAmount(), incomeDate);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Income income = (Income) obj;
        return id == income.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
