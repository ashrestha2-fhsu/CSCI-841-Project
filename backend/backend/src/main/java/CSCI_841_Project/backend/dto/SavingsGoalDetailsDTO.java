package csci812_project.backend.dto;

import csci812_project.backend.enums.ContributionFrequency;
import csci812_project.backend.enums.PriorityLevel;
import csci812_project.backend.enums.SavingsGoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//  Saving goal DetailsDTO  to pass on to generate saving goal report in report service
public class SavingsGoalDetailsDTO {
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate deadline;
    private SavingsGoalStatus status;
    private PriorityLevel priorityLevel;
    private ContributionFrequency contributionFrequency;
    private LocalDateTime dateUpdated;

    // Constructor
    public SavingsGoalDetailsDTO(){}
    public SavingsGoalDetailsDTO(String goalName, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline,
                                 SavingsGoalStatus status, PriorityLevel priorityLevel, ContributionFrequency contributionFrequency, LocalDateTime dateUpdated) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.status = status;
        this.priorityLevel = priorityLevel;
        this.contributionFrequency = contributionFrequency;
        this.dateUpdated = dateUpdated;
    }

    // Getter and Setter

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public SavingsGoalStatus getStatus() {
        return status;
    }

    public void setStatus(SavingsGoalStatus status) {
        this.status = status;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public ContributionFrequency getContributionFrequency() {
        return contributionFrequency;
    }

    public void setContributionFrequency(ContributionFrequency contributionFrequency) {
        this.contributionFrequency = contributionFrequency;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
