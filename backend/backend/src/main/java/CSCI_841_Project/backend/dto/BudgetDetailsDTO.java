package csci812_project.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//  Budget DetailsDTO  to pass to generate Budget report in report service
public class BudgetDetailsDTO {
    private String description;
    private BigDecimal amountLimit;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal spent;
//    private BigDecimal spent = BigDecimal.ZERO;
    private int percentageUsed;
    private String budgetType;
    private BigDecimal rolloverAmount;
    private LocalDateTime dateCreated;
    private String category;

    // Constructor
    public BudgetDetailsDTO(){}
    public BudgetDetailsDTO(String description, BigDecimal amountLimit, LocalDate startDate, LocalDate endDate, String budgetType,
                            BigDecimal rolloverAmount, LocalDateTime dateCreated, String category, BigDecimal spent, int percentageUsed) {
        this.description = description;
        this.amountLimit = amountLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetType = budgetType;
        this.rolloverAmount = rolloverAmount;
        this.dateCreated = dateCreated;
        this.category = category;
        this.spent = spent;
        this.percentageUsed = percentageUsed;
    }

    // Getter and Setter

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getSpent() {return spent;
    }
    public void setSpent(BigDecimal spent) {this.spent = spent;
    }

    public int getPercentageUsed() {return percentageUsed;
    }
    public void setPercentageUsed(int percentageUsed) {this.percentageUsed = percentageUsed;
    }
}
