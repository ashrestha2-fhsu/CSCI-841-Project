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
public class LoanReportDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfLoans;
    private BigDecimal totalLoanBorrowed;
    private BigDecimal totalOutstandingBalance;
    private BigDecimal totalAmountPaid;
    private List<LoanDetailsDTO> loans;
}
