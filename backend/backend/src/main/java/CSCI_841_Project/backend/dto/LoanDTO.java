package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.LoanStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {

    private Long loanId;
    private Long userId;
    private String lenderName;
    private BigDecimal amountBorrowed;
    private int numberOfYears;
    private BigDecimal outstandingBalance;
    private BigDecimal totalOutstandingBalance;
    private BigDecimal totalLoanBorrowed;
    private int numberOfLoans;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private LocalDate dueDate;
    private LoanStatus status;
    private LocalDateTime dateCreated;
}
