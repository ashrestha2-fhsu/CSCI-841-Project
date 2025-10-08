package CSCI_841_Project.backend.dto;

import java.math.BigDecimal;

 //  Loan DetailsDTO  to pass to generate loan report in report service
public class LoanDetailsDTO {
    private String lenderName;
    private BigDecimal amountBorrowed;
    private BigDecimal outstandingBalance;
    private BigDecimal monthlyPayment;

    // Constructor
    public LoanDetailsDTO(){}
    public LoanDetailsDTO(String lenderName, BigDecimal amountBorrowed, BigDecimal outstandingBalance, BigDecimal monthlyPayment) {
        this.lenderName = lenderName;
        this.amountBorrowed = amountBorrowed;
        this.outstandingBalance = outstandingBalance;
        this.monthlyPayment = monthlyPayment;
    }

    // ✅ Getters and Setters
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

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
}