package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.TransactionDTO;
import CSCI_841_Project.backend.dto.TransactionDetailsDTO;
import CSCI_841_Project.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Deposit money into an account.
     */
    @PostMapping("/deposit/{userId}/{accountId}")
    public ResponseEntity<TransactionDTO> deposit(
            @PathVariable Long userId,
            @PathVariable Long accountId,
            @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.deposit(userId, accountId, transactionDTO));
    }


    /**
     * Withdraw money from an account.
     */
    @PostMapping("/withdraw/{userId}/{accountId}")
    public ResponseEntity<TransactionDTO> withdraw(
            @PathVariable Long userId,
            @PathVariable Long accountId,
            @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.withdraw(userId, accountId, transactionDTO));
    }

    /**
     * Transfer money between two accounts.
     */
    @PostMapping("/transfer/{userId}/{fromAccountId}/{toAccountId}")
    public ResponseEntity<TransactionDTO> transfer(
            @PathVariable Long userId,
            @PathVariable Long fromAccountId,
            @PathVariable Long toAccountId,
            @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.transfer(userId, fromAccountId, toAccountId, transactionDTO));
    }

    /**
     * Retrieve all transactions by user ID.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDetailsDTO>> getTransactionsByUser(@PathVariable Long userId) {
        List<TransactionDetailsDTO> details = transactionService.getTransactionDetailsByUser(userId);
        return ResponseEntity.ok(details);
    }

    /**
     * Retrieve all transactions for a specific account.
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    /**
     * Retrieve all transactions by transaction type.
     */
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(@PathVariable String transactionType) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(transactionType));
    }

    /**
     * Upload and process a CSV file for importing transactions.
     */
    @PostMapping("/import/csv")
    public ResponseEntity<String> importTransactionsFromCSV(@RequestParam("file") MultipartFile file, @RequestParam Long userId) {
        transactionService.importTransactionsFromCSV(file, userId);
        return ResponseEntity.ok("Transactions imported successfully.");
    }

    @PostMapping("/manual")
    public ResponseEntity<TransactionDTO> addManualTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.addManualTransaction(transactionDTO));
    }

    @PostMapping("/recurring")
    public ResponseEntity<TransactionDTO> createRecurringTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.createRecurringTransaction(transactionDTO));
    }

    /**
     * ✅ Manually triggers the processing of recurring transactions.
     * This is useful for testing or admin purposes.
     */
    @PostMapping("/process")
    public ResponseEntity<String> processRecurringTransactions() {
        transactionService.processRecurringTransactions();
        return ResponseEntity.ok("Recurring transactions processed successfully.");
    }
}
