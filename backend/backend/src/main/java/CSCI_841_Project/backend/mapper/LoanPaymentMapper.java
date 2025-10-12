package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;
import CSCI_841_Project.backend.entity.Loan;
import CSCI_841_Project.backend.entity.LoanPayment;
import CSCI_841_Project.backend.entity.User;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class LoanPaymentMapper {

    public LoanPaymentDTO toDTO(LoanPayment loanPayment) {
        if (loanPayment == null) return null;

        LoanPaymentDTO dto = new LoanPaymentDTO();
        dto.setPaymentId(loanPayment.getPaymentId());
        dto.setLoanId(loanPayment.getLoan().getLoanId());
        dto.setUserId(loanPayment.getUser().getUserId());
        dto.setPaymentAmount(loanPayment.getPaymentAmount());
        dto.setExtraPayment(loanPayment.getExtraPayment());
        dto.setPrincipalPaid(loanPayment.getPrincipalPaid());
        dto.setInterestPaid(loanPayment.getInterestPaid());
        dto.setTotalAmountPaid(loanPayment.getTotalAmountPaid());
        dto.setPaymentDate(loanPayment.getPaymentDate());
        dto.setRemainingBalance(loanPayment.getRemainingBalance());
        dto.setLastPaymentDate(loanPayment.getLastPaymentDate());
        dto.setNextDueDate(loanPayment.getNextDueDate());

        return dto;
    }

    public LoanPayment toEntity(LoanPaymentDTO dto, Loan loan, User user) {
        if (dto == null) return null;

        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(loan);
        loanPayment.setUser(user);
        loanPayment.setPaymentAmount(dto.getPaymentAmount());
        loanPayment.setExtraPayment(dto.getExtraPayment());
        loanPayment.setPrincipalPaid(dto.getPrincipalPaid());
        loanPayment.setInterestPaid(dto.getInterestPaid());
        loanPayment.setTotalAmountPaid(dto.getTotalAmountPaid());
        loanPayment.setPaymentDate(LocalDateTime.now());
        loanPayment.setRemainingBalance(dto.getRemainingBalance());
        loanPayment.setLastPaymentDate(LocalDateTime.now().toLocalDate());
        loanPayment.setNextDueDate(LocalDateTime.now().toLocalDate().plusMonths(1));

        return loanPayment;
    }
}