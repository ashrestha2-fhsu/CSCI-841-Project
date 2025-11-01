package CSCI_841_Project.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoalDTO {

    /** Unique savings goal ID */
    private Long goalId;

    /** User ID associated with the savings goal */
    private Long userId;

    /** Name of the savings goal (e.g., "Vacation Fund", "Emergency Savings") */
    private String goalName;

    private Long accountId;

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

}
