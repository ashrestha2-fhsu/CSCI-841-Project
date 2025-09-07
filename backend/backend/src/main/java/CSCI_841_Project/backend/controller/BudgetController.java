package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.BudgetDTO;
import CSCI_841_Project.backend.dto.BudgetReportDTO;
import CSCI_841_Project.backend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private  BudgetService budgetService;

    /**
     * ✅ Create a new budget.
     */
    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody BudgetDTO budgetDTO) {
        return ResponseEntity.ok(budgetService.createBudget(budgetDTO));
    }

    /**
     * ✅ Get a budget by its ID.
     */
    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long budgetId) {
        return ResponseEntity.ok(budgetService.getBudgetById(budgetId));
    }

    /**
     * ✅ Get all budgets for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetDTO>> getBudgetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUser(userId));
    }

    /**
     * ✅ Update an existing budget.
     */
    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long budgetId, @RequestBody BudgetDTO budgetDTO) {
        return ResponseEntity.ok(budgetService.updateBudget(budgetId, budgetDTO));
    }

    /**
     * ✅ Soft delete a budget (mark as deleted).
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<String> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.ok("Budget deleted successfully.");
    }

    @GetMapping("/report")
    public ResponseEntity<BudgetReportDTO> getBudgetReport(@RequestParam Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetReport(userId));
    }

}

