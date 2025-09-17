package com.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Budget model class representing spending limits for categories.

 */
public class Budget {
    
    private int id;
    private int categoryId;
    private Category category;
    private BigDecimal amount;
    private BudgetPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    
    // Enum for budget periods
    public enum BudgetPeriod {
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        YEARLY("Yearly");
        
        private final String displayName;
        
        BudgetPeriod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public Budget() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Budget(int categoryId, BigDecimal amount, BudgetPeriod period, 
                  LocalDate startDate, LocalDate endDate) {
        this();
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Budget(int id, int categoryId, BigDecimal amount, BudgetPeriod period, 
                  LocalDate startDate, LocalDate endDate, LocalDateTime createdAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }
    
    // Business logic method: Check if budget is currently active
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
    
    // Business logic method: Check if a date falls within budget period
    public boolean isDateInPeriod(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
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
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Budget amount must be positive");
        }
    }
    
    public BudgetPeriod getPeriod() {
        return period;
    }
    
    public void setPeriod(BudgetPeriod period) {
        this.period = period;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
        String categoryName = category != null ? category.getName() : "Category " + categoryId;
        return String.format("%s - %s (%s)", categoryName, getFormattedAmount(), period.getDisplayName());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Budget budget = (Budget) obj;
        return id == budget.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
