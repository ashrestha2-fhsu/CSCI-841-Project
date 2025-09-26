package csci841_project.backend.controller;

import csci841_project.backend.dto.LoanDTO;
import csci841_project.backend.dto.LoanPaymentRequest;
import csci841_project.backend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;


    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanDTO loanDTO) {
        return ResponseEntity.ok(loanService.createLoan(loanDTO));
    }

    @PutMapping("/{loanId}")
    public ResponseEntity<LoanDTO> updateLoan(@PathVariable Long loanId, @RequestBody LoanDTO loanDTO) {
        return ResponseEntity.ok(loanService.updateLoan(loanId, loanDTO));
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDTO>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUser(userId));
    }


    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }


    // ✅ Allow admin to manually update loan status
    @PutMapping("/{loanId}/update-status")
    public ResponseEntity<String> updateLoanStatus(@PathVariable Long loanId) {
        loanService.updateLoanStatus(loanId);
        return ResponseEntity.ok("Loan status updated successfully.");
    }

    // ✅ Allow admin or user to manually update next due date
    @PutMapping("/{loanId}/update-due-date")
    public ResponseEntity<String> updateNextDueDate(@PathVariable Long loanId) {
        loanService.updateNextDueDate(loanId);
        return ResponseEntity.ok("Loan due date updated successfully.");
    }


}
