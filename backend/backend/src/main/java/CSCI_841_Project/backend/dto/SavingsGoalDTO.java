package csci812_project.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class SavingsGoalDTO {

    /** Unique savings goal ID */
    private Long goalId;

    /** User ID associated with the savings goal */
    private Long userId;

    /** Name of the savings goal (e.g., "Vacation Fund", "Emergency Savings") */
    private String goalName;

    /** Target savings amount for this goal */
    private BigDecimal targetAmount;

    /** Current amount saved towards the goal */
    private BigDecimal currentAmount;

    /** Deadline for achieving the savings goal */
    private LocalDate deadline;

    /** Status of the savings goal (ACTIVE, COMPLETED, CANCELLED) */
    private String status;

    /** Indicates whether contributions to this goal should be automated */
    private boolean autoSave;

    /** Priority level for the savings goal (LOW, MEDIUM, HIGH) */
    private String priorityLevel;

    /** Frequency of contributions (DAILY, WEEKLY, MONTHLY, CUSTOM) */
    private String contributionFrequency;

    /** Indicates whether the savings goal is deleted (soft delete) */
    private boolean isDeleted;

    /** Timestamp for when the savings goal was created */
    private LocalDateTime dateCreated;

    /** Timestamp for when the savings goal was last updated */
    private LocalDateTime dateUpdated;

    // Constructor
    public SavingsGoalDTO(){}
    public SavingsGoalDTO(Long goalId, Long userId, String goalName, BigDecimal targetAmount, BigDecimal currentAmount,
                          LocalDate deadline, String status, boolean autoSave, String priorityLevel, String contributionFrequency,
                          boolean isDeleted, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.status = status;
        this.autoSave = autoSave;
        this.priorityLevel = priorityLevel;
        this.contributionFrequency = contributionFrequency;
        this.isDeleted = isDeleted;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    // Getter and Setter
    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long id) {
        this.goalId = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getContributionFrequency() {
        return contributionFrequency;
    }

    public void setContributionFrequency(String contributionFrequency) {
        this.contributionFrequency = contributionFrequency;
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
}

