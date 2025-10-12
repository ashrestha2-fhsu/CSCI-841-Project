package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanDTO {

    private Long loanId;
    private Long userId;
    private String lenderName;
    private BigDecimal amountBorrowed;
    private int numberOfYears;
    private BigDecimal outstandingBalance;
    private BigDecimal totalOutstandingBalance;
    private BigDecimal totalLoanBorrowed;
    private int numberOfLoans;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private LocalDate dueDate;
    private LoanStatus status;
    private LocalDateTime dateCreated;


    // Constructor
    public LoanDTO(){}

    public LoanDTO(Long loanId, Long userId, String lenderName, BigDecimal amountBorrowed, int numberOfYears, BigDecimal outstandingBalance,
                   BigDecimal totalOutstandingBalance, BigDecimal totalLoanBorrowed, int numberOfLoans, BigDecimal interestRate,
                   BigDecimal monthlyPayment, LocalDate dueDate, LoanStatus status, LocalDateTime dateCreated) {
        this.loanId = loanId;
        this.userId = userId;
        this.lenderName = lenderName;
        this.amountBorrowed = amountBorrowed;
        this.numberOfYears = numberOfYears;
        this.outstandingBalance = outstandingBalance;
        this.totalOutstandingBalance = totalOutstandingBalance;
        this.totalLoanBorrowed = totalLoanBorrowed;
        this.numberOfLoans = numberOfLoans;
        this.interestRate = interestRate;
        this.monthlyPayment = monthlyPayment;
        this.dueDate = dueDate;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    // Getter and Setter
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public BigDecimal getAmountBorrowed() {
        return amountBorrowed;
    }

    public void setAmountBorrowed(BigDecimal amountBorrowed) {
        this.amountBorrowed = amountBorrowed;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }


    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public int getNumberOfYears() {
        return numberOfYears;
    }

    public void setNumberOfYears(int numberOfYears) {
        this.numberOfYears = numberOfYears;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalOutstandingBalance() {
        return totalOutstandingBalance;
    }

    public void setTotalOutstandingBalance(BigDecimal totalOutstandingBalance) {
        this.totalOutstandingBalance = totalOutstandingBalance;
    }

    public BigDecimal getTotalLoanBorrowed() {
        return totalLoanBorrowed;
    }

    public void setTotalLoanBorrowed(BigDecimal totalLoanBorrowed) {
        this.totalLoanBorrowed = totalLoanBorrowed;
    }

    public int getNumberOfLoans() {
        return numberOfLoans;
    }

    public void setNumberOfLoans(int numberOfLoans) {
        this.numberOfLoans = numberOfLoans;
    }
}
