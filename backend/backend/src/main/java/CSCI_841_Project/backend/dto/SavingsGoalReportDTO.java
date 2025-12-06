package CSCI_841_Project.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoalReportDTO {

    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSaved;
    private BigDecimal totalTarget;
    private BigDecimal progress;
    private List<SavingsGoalDetailsDTO> savingsGoals;
}