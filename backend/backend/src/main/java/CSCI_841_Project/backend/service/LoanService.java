package csci841_project.backend.service;

import csci841_project.backend.dto.LoanDTO;
import csci841_project.backend.entity.User;

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
