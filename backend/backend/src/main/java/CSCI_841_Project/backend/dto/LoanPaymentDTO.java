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
public class LoanPaymentDTO {

    private Long paymentId;
    private Long loanId;
    private Long userId;
    private BigDecimal paymentAmount;
    private BigDecimal extraPayment;
    private BigDecimal totalAmountPaid;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;
    private LocalDateTime paymentDate;
    private BigDecimal remainingBalance;
    private LocalDate lastPaymentDate;
    private LocalDate nextDueDate;
}
