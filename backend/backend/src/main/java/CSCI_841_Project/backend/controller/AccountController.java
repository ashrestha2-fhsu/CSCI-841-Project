package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.AccountDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.mapper.AccountMapper;
import CSCI_841_Project.backend.repository.AccountRepository;
import CSCI_841_Project.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountMapper accountMapper;

    // API TO CREATE NEW ACCOUNT +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @PostMapping("/create/{userId}")
    public ResponseEntity<AccountDTO> createAccount(@PathVariable Long userId, @RequestBody AccountDTO accountDTO){
        return ResponseEntity.ok(accountService.createAccount(userId, accountDTO));
    }

    /**
     * Retrieves account details by account ID.
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isPresent()) {
            return ResponseEntity.ok(accountMapper.toDTO(account.get())); // ✅ 200 OK if found
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Not Found"); // ✅ 404 Response
        }
    }


    /**
     * Retrieves all accounts for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    /**
     * Updates an existing account.
     */
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long accountId, @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(accountId, accountDTO));
    }

    /**
     * Soft deletes an account.
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok("Account has been soft deleted.");
    }

    /**
     * Restores a previously soft-deleted account.
     */
    @PutMapping("/{accountId}/restore")
    public ResponseEntity<String> restoreAccount(@PathVariable Long accountId) {
        accountService.restoreAccount(accountId);
        return ResponseEntity.ok("Account has been restored.");
    }

    //    Get All account Both Soft-Delete and Non-Delete
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<AccountDTO>> getAllAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAllAccountsByUser(userId));
    }

}
