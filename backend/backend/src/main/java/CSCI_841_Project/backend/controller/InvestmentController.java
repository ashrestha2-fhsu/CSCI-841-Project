package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;



    /**
     * ✅ Add a new investment.
     */
    @PostMapping
    public ResponseEntity<InvestmentDTO> addInvestment(@RequestBody InvestmentDTO investmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.addInvestment(investmentDTO));
    }

    /**
     * ✅ Update an investment.
     */
    @PutMapping("/{investmentId}")
    public ResponseEntity<InvestmentDTO> updateInvestment(
            @PathVariable Long investmentId,
            @RequestBody InvestmentDTO investmentDTO) {
        return ResponseEntity.ok(investmentService.updateInvestment(investmentId, investmentDTO));
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


}

