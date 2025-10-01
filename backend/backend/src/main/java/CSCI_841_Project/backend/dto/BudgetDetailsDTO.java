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
//  Budget DetailsDTO  to pass to generate Budget report in report service
public class BudgetDetailsDTO {
    private Long budgetId;
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

    // Getter and Setter

}
