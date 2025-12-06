package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.ContributionFrequency;
import CSCI_841_Project.backend.enums.PriorityLevel;
import CSCI_841_Project.backend.enums.SavingsGoalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

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
}