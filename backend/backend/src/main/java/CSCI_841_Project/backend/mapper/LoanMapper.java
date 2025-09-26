package csci841_project.backend.mapper;

import csci841_project.backend.dto.LoanDTO;
import csci841_project.backend.entity.Loan;
import csci841_project.backend.entity.User;
import csci841_project.backend.enums.LoanStatus;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanDTO toDTO(Loan loan) {
        if (loan == null) return null;

        LoanDTO dto = new LoanDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setUserId(loan.getUser().getUserId());
        dto.setLenderName(loan.getLenderName());
        dto.setAmountBorrowed(loan.getAmountBorrowed());
        dto.setOutstandingBalance(loan.getOutstandingBalance());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTotalLoanBorrowed(loan.getTotalLoanBorrowed());
        dto.setNumberOfLoans(loan.getNumberOfLoans());
        dto.setTotalOutstandingBalance(loan.getTotalOutstandingBalance());
        dto.setMonthlyPayment(loan.getMonthlyPayment());
        dto.setNumberOfYears(loan.getNumberOfYears());
        dto.setDueDate(loan.getDueDate());
        dto.setStatus(loan.getStatus());

        return dto;
    }

    public Loan toEntity(LoanDTO dto, User user) {
        if (dto == null) return null;

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLenderName(dto.getLenderName());
        loan.setAmountBorrowed(dto.getAmountBorrowed());
        loan.setOutstandingBalance(dto.getOutstandingBalance());
        loan.setTotalLoanBorrowed(dto.getTotalLoanBorrowed());
        loan.setNumberOfLoans(dto.getNumberOfLoans());
        loan.setTotalOutstandingBalance(dto.getTotalOutstandingBalance());
        loan.setInterestRate(dto.getInterestRate());
        loan.setMonthlyPayment(dto.getMonthlyPayment());
        loan.setNumberOfYears(dto.getNumberOfYears());
        loan.setDueDate(dto.getDueDate());
        loan.setStatus(dto.getStatus());

        return loan;
    }
}
