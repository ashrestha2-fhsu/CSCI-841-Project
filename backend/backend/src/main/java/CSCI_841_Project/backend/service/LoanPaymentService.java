package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LoanPaymentService {

    LoanPaymentDTO makePayment(Long loanId,
                              BigDecimal paymentAmount,
                              BigDecimal extraPayment,
                              String paymentMethod,          // NEW
                              Long accountId,                // NEW (required if INTERNAL_ACCOUNT)
                              String externalReference,      // NEW (optional)
                              LocalDate paymentDate          // NEW (optional; null -> now)
    );

    List<LoanPaymentDTO> getPaymentsByLoan(Long loanId);

    void sendLoanPaymentReminders();

}
