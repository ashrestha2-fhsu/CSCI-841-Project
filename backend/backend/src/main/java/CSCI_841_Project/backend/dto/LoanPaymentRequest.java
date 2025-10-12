package CSCI_841_Project.backend.dto;

import java.math.BigDecimal;

public class LoanPaymentRequest {
    private BigDecimal paymentAmount;
    private BigDecimal extraPayment;

    // ✅ Getters and Setters
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
}