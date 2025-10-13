package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Loan;
import CSCI_841_Project.backend.entity.LoanPayment;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.PaymentMethod;
import org.springframework.stereotype.Component;

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

        // paymentMethod as String (enum name)
        dto.setPaymentMethod(
                loanPayment.getPaymentMethod() == null ? null : loanPayment.getPaymentMethod().name()
        );

        // accountId (nullable)
        if (loanPayment.getAccount() != null) {
            dto.setAccountId(loanPayment.getAccount().getAccountId());
            // If your DTO has accountName, populate it too:
            try {
                dto.setAccountName(loanPayment.getAccount().getName());
            } catch (NoSuchMethodError | RuntimeException ignore) {
                // accountName not present in DTO or Account has no name; ignore safely
            }
        }

        // external reference (nullable)
        dto.setExternalReference(loanPayment.getExternalReference());


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

        // paymentMethod from String to enum (default INTERNAL_ACCOUNT if null/blank)
        PaymentMethod method = null;
        if (dto.getPaymentMethod() != null && !dto.getPaymentMethod().isBlank()) {
            try {
                method = PaymentMethod.valueOf(dto.getPaymentMethod());
            } catch (IllegalArgumentException e) {
                // fallback if client sent an unknown value
                method = PaymentMethod.INTERNAL_ACCOUNT;
            }
        } else {
            method = PaymentMethod.INTERNAL_ACCOUNT;
        }
        loanPayment.setPaymentMethod(method);

        // accountId -> Account reference (nullable)
        if (dto.getAccountId() != null) {
            Account accountRef = new Account();
            accountRef.setAccountId(dto.getAccountId()); // JPA will treat as reference
            loanPayment.setAccount(accountRef);
        } else {
            loanPayment.setAccount(null);
        }

        // external reference
        loanPayment.setExternalReference(dto.getExternalReference());

        return loanPayment;
    }
}