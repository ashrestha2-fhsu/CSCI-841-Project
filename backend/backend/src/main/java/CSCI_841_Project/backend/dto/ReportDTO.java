package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.ReportFileFormat;
import CSCI_841_Project.backend.enums.ReportGeneratedBy;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {

    /** Unique report ID */
    private Long reportId;
    private Long userId;
    /** Start and end dates for the report */
    private LocalDate startDate;
    private LocalDate endDate;
    /** Financial summary */
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    /** Report metadata */
    private ReportGeneratedBy generatedBy;
    private ReportFileFormat fileFormat;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;

}