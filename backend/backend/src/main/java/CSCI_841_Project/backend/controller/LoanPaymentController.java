package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;
import CSCI_841_Project.backend.dto.LoanPaymentRequest;
import CSCI_841_Project.backend.service.LoanPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loan-payments")
public class LoanPaymentController {

    @Autowired
    private LoanPaymentService loanPaymentService;

    @PostMapping("/{loanId}/pay")
    public ResponseEntity<LoanPaymentDTO> makePayment(
            @PathVariable Long loanId,
            @RequestParam(required = false) BigDecimal paymentAmount,
            @RequestParam(required = false) BigDecimal extraPayment,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String externalReference,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate
    ) {
        return ResponseEntity.ok(
                loanPaymentService.makePayment(
                        loanId, paymentAmount, extraPayment, paymentMethod, accountId, externalReference, paymentDate
                )
        );
    }

//    @PostMapping("/{loanId}/pay")
//    public ResponseEntity<LoanPaymentDTO> makePayment(
//            @PathVariable Long loanId,
//            @RequestBody LoanPaymentRequest paymentRequest) {
//
//        LoanPaymentDTO paymentResponse = loanPaymentService.makePayment(
//                loanId,
//                paymentRequest.getPaymentAmount(),
//                paymentRequest.getExtraPayment()
//        );
//
//        return ResponseEntity.ok(paymentResponse);
//    }

    @GetMapping("/{loanId}")
    public ResponseEntity<List<LoanPaymentDTO>> getPaymentsByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanPaymentService.getPaymentsByLoan(loanId));
    }

    /**
     * ✅ Trigger Loan Reminder Emails Manually
     */
    @PostMapping("/send-reminders")
    public ResponseEntity<String> triggerLoanReminders() {
        loanPaymentService.sendLoanPaymentReminders();
        return ResponseEntity.ok("Loan reminders sent!");
    }

}