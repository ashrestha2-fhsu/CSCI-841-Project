package CSCI_841_Project.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//  Budget ReportDTO to pass on Budget attributes in Budget reported data in report service
public class BudgetReportDTO {
    private Long userId;
    private BigDecimal totalBudgetLimit;
    private BigDecimal totalRolloverAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<BudgetDetailsDTO> budgets; 

    // Constructor
    public BudgetReportDTO(){}
    public BudgetReportDTO(Long userId, BigDecimal totalBudgetLimit, BigDecimal totalRolloverAmount, LocalDate startDate,
                           LocalDate endDate, List<BudgetDetailsDTO> budgets) {
        this.userId = userId;
        this.totalBudgetLimit = totalBudgetLimit;
        this.totalRolloverAmount = totalRolloverAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgets = budgets;
    }

    // Getter and Setter

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalBudgetLimit() {
        return totalBudgetLimit;
    }

    public void setTotalBudgetLimit(BigDecimal totalBudgetLimit) {
        this.totalBudgetLimit = totalBudgetLimit;
    }

    public BigDecimal getTotalRolloverAmount() {
        return totalRolloverAmount;
    }

    public void setTotalRolloverAmount(BigDecimal totalRolloverAmount) {
        this.totalRolloverAmount = totalRolloverAmount;
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

    public List<BudgetDetailsDTO> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<BudgetDetailsDTO> budgets) {
        this.budgets = budgets;
    }
}

