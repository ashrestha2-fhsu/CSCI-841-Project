package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LoanPaymentService {

    LoanPaymentDTO makePayment(Long loanId, BigDecimal paymentAmount, BigDecimal extraPayment);

    List<LoanPaymentDTO> getPaymentsByLoan(Long loanId);

    void sendLoanPaymentReminders();

}