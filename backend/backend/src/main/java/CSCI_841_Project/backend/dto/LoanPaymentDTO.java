package CSCI_841_Project.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanPaymentDTO {
    private Long paymentId;
    private Long loanId;
    private Long userId;
    private BigDecimal paymentAmount;
    private BigDecimal extraPayment;
    private BigDecimal totalAmountPaid;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;
    private LocalDateTime paymentDate;
    private BigDecimal remainingBalance;
    private LocalDate lastPaymentDate;
    private LocalDate nextDueDate;



    // Constructor
    public LoanPaymentDTO(){}
    public LoanPaymentDTO(Long paymentId, Long loanId, Long userId, BigDecimal paymentAmount, BigDecimal extraPayment,
                          BigDecimal totalAmountPaid, BigDecimal interestPaid, BigDecimal principalPaid, LocalDateTime paymentDate,
                          BigDecimal remainingBalance, LocalDate lastPaymentDate, LocalDate nextDueDate) {
        this.paymentId = paymentId;
        this.loanId = loanId;
        this.userId = userId;
        this.paymentAmount = paymentAmount;
        this.extraPayment = extraPayment;
        this.totalAmountPaid = totalAmountPaid;
        this.interestPaid = interestPaid;
        this.principalPaid = principalPaid;
        this.paymentDate = paymentDate;
        this.remainingBalance = remainingBalance;
        this.lastPaymentDate = lastPaymentDate;
        this.nextDueDate = nextDueDate;
    }


    // Getter and Setter

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getExtraPayment() {
        return extraPayment;
    }

    public void setExtraPayment(BigDecimal extraPayment) {
        this.extraPayment = extraPayment;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}