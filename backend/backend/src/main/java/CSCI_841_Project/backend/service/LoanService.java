package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.LoanDTO;
import CSCI_841_Project.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    LoanDTO createLoan(LoanDTO loanDTO);

    LoanDTO getLoanById(Long loanId);

    List<LoanDTO> getLoansByUser(Long userId);

    LoanDTO updateLoan(Long loanId, LoanDTO loanDTO);

    void deleteLoan(Long loanId);

    void updateLoanStatus(Long loanId);

    void updateNextDueDate(Long loanId);

    void updateLoanStats(User user);

}