package csci812_project.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BudgetDTO {

    /** Unique budget ID */
    private Long budgetId;

    /** User ID associated with the budget */
    private Long userId;

    /** Category ID associated with the budget */
    private Long categoryId;

    /** Maximum spending amount allowed within the budget period */
    private BigDecimal amountLimit;

    private String description;

    /** Start date of the budget period */
    private LocalDate startDate;

    /** End date of the budget period */
    private LocalDate endDate;

    /** Budget type (STRICT or FLEXIBLE) */
    private String budgetType;


    /** Amount that rolls over to the next period (if applicable) */
    private BigDecimal rolloverAmount;

    /** Indicates whether the budget is deleted (soft delete) */
    private boolean isDeleted;

    /** Timestamp for when the budget was created */
    private LocalDateTime dateCreated;

    /** Timestamp for when the budget was last updated */
    private LocalDateTime dateUpdated;

    // Constructor
    public BudgetDTO(){}
    public BudgetDTO(Long budgetId, Long userId, Long categoryId, BigDecimal amountLimit, LocalDate startDate, LocalDate endDate,
                     String budgetType, BigDecimal rolloverAmount, boolean isDeleted, LocalDateTime dateCreated,
                     LocalDateTime dateUpdated, String description) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amountLimit = amountLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetType = budgetType;
        this.rolloverAmount = rolloverAmount;
        this.isDeleted = isDeleted;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.description = description;
    }

    // Getter and Setter
    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmountLimit() {
        return amountLimit;
    }

    public void setAmountLimit(BigDecimal amountLimit) {
        this.amountLimit = amountLimit;
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

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public BigDecimal getRolloverAmount() {
        return rolloverAmount;
    }

    public void setRolloverAmount(BigDecimal rolloverAmount) {
        this.rolloverAmount = rolloverAmount;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
