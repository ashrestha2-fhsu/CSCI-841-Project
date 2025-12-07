package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.dto.InvestmentHistoryDTO;
import CSCI_841_Project.backend.dto.InvestmentReportDTO;
import CSCI_841_Project.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    /**
     * ✅ Add a new investment.
     */
    @PostMapping
    public ResponseEntity<InvestmentDTO> addInvestment(@RequestBody InvestmentDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(investmentService.addInvestment(dto));  // ✅ No userId in dto!
    }


    /**
     * ✅ Update an investment.
     */
    @PutMapping("/{investmentId}")
    public ResponseEntity<InvestmentDTO> updateInvestment(
            @PathVariable Long investmentId,
            @RequestBody InvestmentDTO investmentDTO,
            Principal principal) {

        return ResponseEntity.ok(
                investmentService.updateInvestment(investmentId, investmentDTO, principal.getName())
        );
    }

    @PostMapping("/{investmentId}/reinvest")
    public ResponseEntity<InvestmentDTO> reinvest(
            @PathVariable Long investmentId,
            @RequestBody Map<String, Object> payload,
            Principal principal) {

        BigDecimal amountInvested = new BigDecimal(payload.get("amountInvested").toString());
        BigDecimal reinvestedQuantity = new BigDecimal(payload.get("reinvestedQuantity").toString()); // ✅ Ensure this name matches
        String username = principal.getName();

        InvestmentDTO updated = investmentService.reinvest(investmentId, amountInvested, reinvestedQuantity, username);
        return ResponseEntity.ok(updated);
    }


    /**
     * ✅ Get an investment by ID.
     */
    @GetMapping("/{investmentId}")
    public ResponseEntity<InvestmentDTO> getInvestmentById(@PathVariable Long investmentId) {
        return ResponseEntity.ok(investmentService.getInvestmentById(investmentId));
    }

    /**
     * ✅ Get all investments for a user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByUser(userId));
    }

    /**
     * ✅ Delete an investment.
     */
    @DeleteMapping("/{investmentId}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long investmentId) {
        investmentService.deleteInvestment(investmentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ Restore a deleted investment.
     */
    @PutMapping("/{investmentId}/restore")
    public ResponseEntity<Void> restoreInvestment(@PathVariable Long investmentId) {
        investmentService.restoreInvestment(investmentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ Trigger manual simulation (for testing).
     */
    @PostMapping("/simulate-growth")
    public ResponseEntity<String> simulateGrowth() {
        investmentService.simulateInvestmentGrowth();
        return ResponseEntity.ok("Simulated investment growth updated!");
    }

    @GetMapping("/report")
    public ResponseEntity<InvestmentReportDTO> getInvestmentReport(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        InvestmentReportDTO report = investmentService.getInvestmentReport(userId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{investmentId}/history")
    public ResponseEntity<List<InvestmentHistoryDTO>> getInvestmentHistory(@PathVariable Long investmentId) {
        List<InvestmentHistoryDTO> history = investmentService.getInvestmentHistory(investmentId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}/symbol")
    public ResponseEntity<InvestmentDTO> findBySymbol(
            @PathVariable Long userId,
            @RequestParam String symbol) {

        return investmentService.findBySymbol(userId, symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

