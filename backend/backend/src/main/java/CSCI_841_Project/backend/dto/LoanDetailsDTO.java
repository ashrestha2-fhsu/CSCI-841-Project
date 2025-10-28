package CSCI_841_Project.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//  Loan DetailsDTO  to pass to generate loan report in report service
public class LoanDetailsDTO {
    private String lenderName;
    private BigDecimal amountBorrowed;
    private BigDecimal outstandingBalance;
    private BigDecimal monthlyPayment;

}
