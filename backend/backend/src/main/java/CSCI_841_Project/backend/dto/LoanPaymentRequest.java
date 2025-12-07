package CSCI_841_Project.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanPaymentRequest {
    private BigDecimal paymentAmount;
    private BigDecimal extraPayment;

    @jakarta.validation.constraints.NotNull
    private String paymentMethod;       // enum name as String
    private Long accountId;
    private String externalReference;
    private LocalDate paymentDate;


    // ✅ Getters and Setters
}

