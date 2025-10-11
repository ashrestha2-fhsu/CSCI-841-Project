package CSCI_841_Project.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


//   Loan ReportDTO to pass on loan attributes in loan reported data in report service
public class LoanReportDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfLoans;
    private BigDecimal totalLoanBorrowed;
    private BigDecimal totalOutstandingBalance;
    private BigDecimal totalAmountPaid;
    private List<LoanDetailsDTO> loans;


    // Constructor
    public LoanReportDTO(){}
    public LoanReportDTO(Long userId, LocalDate startDate, LocalDate endDate, int numberOfLoans, BigDecimal totalLoanBorrowed,
                         BigDecimal totalOutstandingBalance, BigDecimal totalAmountPaid, List<LoanDetailsDTO> loans) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfLoans = numberOfLoans;
        this.totalLoanBorrowed = totalLoanBorrowed;
        this.totalOutstandingBalance = totalOutstandingBalance;
        this.totalAmountPaid = totalAmountPaid;
        this.loans = loans;
    }


    // ✅ Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getNumberOfLoans() {
        return numberOfLoans;
    }

    public void setNumberOfLoans(int numberOfLoans) {
        this.numberOfLoans = numberOfLoans;
    }

    public BigDecimal getTotalLoanBorrowed() {
        return totalLoanBorrowed;
    }

    public void setTotalLoanBorrowed(BigDecimal totalLoanBorrowed) {
        this.totalLoanBorrowed = totalLoanBorrowed;
    }

    public BigDecimal getTotalOutstandingBalance() {
        return totalOutstandingBalance;
    }

    public void setTotalOutstandingBalance(BigDecimal totalOutstandingBalance) {
        this.totalOutstandingBalance = totalOutstandingBalance;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public List<LoanDetailsDTO> getLoans() {
        return loans;
    }

    public void setLoans(List<LoanDetailsDTO> loans) {
        this.loans = loans;
    }
}