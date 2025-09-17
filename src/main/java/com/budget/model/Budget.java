package com.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Budget model class representing budget limits for categories
 */
public class Budget {
    public enum Period {
        WEEKLY, MONTHLY, YEARLY
    }
    
    private int id;
    private int categoryId;
    private Category category; // For display purposes
    private BigDecimal amount;
    private Period period;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDate;
    
    // Constructors
    public Budget() {}
    
    public Budget(int categoryId, BigDecimal amount, Period period, LocalDate startDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.createdDate = LocalDateTime.now();
        
        // Calculate end date based on period
        calculateEndDate();
    }
    
    public Budget(int id, int categoryId, BigDecimal amount, Period period, 
                  LocalDate startDate, LocalDate endDate, LocalDateTime createdDate) {
        this.id = id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;
    }
    
    // Calculate end date based on period
    private void calculateEndDate() {
        if (startDate != null && period != null) {
            switch (period) {
                case WEEKLY:
                    this.endDate = startDate.plusWeeks(1).minusDays(1);
                    break;
                case MONTHLY:
                    this.endDate = startDate.plusMonths(1).minusDays(1);
                    break;
                case YEARLY:
                    this.endDate = startDate.plusYears(1).minusDays(1);
                    break;
            }
        }
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
    
    public Period getPeriod() {
        return period;
    }
    
    public void setPeriod(Period period) {
        this.period = period;
        calculateEndDate(); // Recalculate end date when period changes
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateEndDate(); // Recalculate end date when start date changes
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    /**
     * Check if the budget is currently active
     */
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
    
    /**
     * Get remaining days in budget period
     */
    public long getRemainingDays() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) {
            return 0;
        }
        return today.until(endDate).getDays() + 1;
    }
    
    @Override
    public String toString() {
        return String.format("Budget{id=%d, category='%s', amount=%.2f, period=%s, startDate=%s, endDate=%s}",
                id, category != null ? category.getName() : "Unknown", 
                amount != null ? amount.doubleValue() : 0.0, period, startDate, endDate);
    }
}
