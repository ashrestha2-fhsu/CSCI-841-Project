package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.TransactionDTO;
import CSCI_841_Project.backend.dto.TransactionDetailsDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Category;
import CSCI_841_Project.backend.entity.Transaction;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RecurringInterval;
import CSCI_841_Project.backend.enums.TransactionStatus;
import CSCI_841_Project.backend.enums.TransactionType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.TransactionMapper;
import CSCI_841_Project.backend.repository.AccountRepository;
import CSCI_841_Project.backend.repository.CategoryRepository;
import CSCI_841_Project.backend.repository.TransactionRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.BudgetService;
import CSCI_841_Project.backend.service.EmailService;
import CSCI_841_Project.backend.service.TransactionService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImplementation implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private BudgetService budgetService;
//    @Autowired
//    private EmailService emailService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private  ScheduledEmailService scheduledEmailService;

    @Override
    public TransactionDTO deposit(Long userId, Long accountId, TransactionDTO transactionDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // ✅ Fetch Category using `categoryId` (instead of passing it in `toEntity()`)
        Category category = categoryRepository.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        account.setBalance(account.getBalance().add(transactionDTO.getAmount()));

        Transaction transaction = transactionMapper.toEntity(transactionDTO, account.getUser(), account, null);
        transaction.setUser(account.getUser());
        transaction.setAccount(account);
        transaction.setDate(transaction.getDate());
        transaction.setCategory(category);

        transactionRepository.save(transaction);
        accountRepository.save(account);
        return transactionMapper.toDTO(transaction);
    }

    @Override
    public TransactionDTO withdraw(Long userId, Long accountId, TransactionDTO transactionDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        // ✅ Fetch Category
        Category category = categoryRepository.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        // ✅ Fetch Parent Transaction if `parentTransactionId` is provided
        Transaction parentTransaction = null;
        if (transactionDTO.getParentTransactionId() != null) {
            parentTransaction = transactionRepository.findById(transactionDTO.getParentTransactionId())
                    .orElseThrow(() -> new RuntimeException("Parent transaction not found"));
        }
        account.setBalance(account.getBalance().subtract(transactionDTO.getAmount()));
        Transaction transaction = transactionMapper.toEntity(transactionDTO, account.getUser(), account, null);
        transaction.setUser(account.getUser());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setParentTransaction(parentTransaction);

        transactionRepository.save(transaction);
        accountRepository.save(account);
        return transactionMapper.toDTO(transaction);
    }

    @Override
    public TransactionDTO transfer(Long userId, Long fromAccountId, Long toAccountId, TransactionDTO transactionDTO) {
        if (toAccountId == null) {
            throw new RuntimeException("Destination account (toAccountId) must not be null!");
        }
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("To account not found"));
        if (fromAccount.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(transactionDTO.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transactionDTO.getAmount()));

        Transaction transaction = transactionMapper.toEntity(transactionDTO, fromAccount.getUser(), fromAccount, toAccount);
        transaction.setUser(fromAccount.getUser());
        transaction.setAccount(fromAccount);
        transaction.setToAccount(toAccount);

        transactionRepository.save(transaction);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return transactionMapper.toDTO(transaction);
    }

    /**
     * Retrieves all transactions for a specific user.
     */
    @Override
    public List<TransactionDetailsDTO> getTransactionDetailsByUser(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);
        return transactions.stream()
                .map(transactionMapper::toDetailsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all transactions for a specific account.
     */
    @Override
    public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
        // ✅ Check if the user exists before fetching accounts
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Account with ID " + accountId + " not found"); // ✅ Throws a 404 error
        }
        List<Transaction> transactions = transactionRepository.findByAccount_AccountId(accountId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByType(String transactionType) {
        try {
            TransactionType type = TransactionType.valueOf(transactionType.toUpperCase());
            List<Transaction> transactions = transactionRepository.findByTransactionType(type);
            return transactions.stream()
                    .map(transactionMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction type: " + transactionType);
        }
    }

    @Override
    public void importTransactionsFromCSV(MultipartFile file, Long userId) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            for (CSVRecord record : csvParser) {
                Long accountId = Long.parseLong(record.get("Account ID"));
                BigDecimal amount = new BigDecimal(record.get("Amount"));
                TransactionType transactionType = TransactionType.valueOf(record.get("Transaction Type").toUpperCase());
                String description = record.get("Description");

                Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setUserId(user.getUserId());
                transactionDTO.setAccountId(account.getAccountId());
                transactionDTO.setAmount(amount);
                transactionDTO.setTransactionType(transactionType.name());
                transactionDTO.setDescription(description);

                // ✅ Pass `transactionDTO`, `user`, and `account` to `toEntity()`
                Transaction transaction = transactionMapper.toEntity(transactionDTO, user, account, null);

                transactionRepository.save(transaction);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public TransactionDTO addManualTransaction(TransactionDTO transactionDTO) {
        User user = userRepository.findById(transactionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Category category = categoryRepository.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // ✅ Handle Parent Transaction (if applicable)
        Transaction parentTransaction = null;
        if (transactionDTO.getParentTransactionId() != null) {
            parentTransaction = transactionRepository.findById(transactionDTO.getParentTransactionId())
                    .orElseThrow(() -> new RuntimeException("Parent transaction not found"));
        }
        // ✅ Budget validation (STRICT prevents, FLEXIBLE allows)
        boolean allowed = budgetService.isTransactionWithinBudget(
                user.getUserId(), category.getCategoryId(), transactionDTO.getAmount(),
                LocalDateTime.now()
        );
        if (!allowed) {
            throw new RuntimeException("❌ This transaction exceeds your budget limit for this category.");
        }
        // ✅ Convert DTO to Entity
        Transaction transaction = transactionMapper.toEntity(transactionDTO, user, account, null);
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setParentTransaction(parentTransaction);  // ✅ Set Parent Transaction

        transaction = transactionRepository.save(transaction);

        // ✅ Alert logic: only notify if FLEXIBLE and exceeded 80%
        boolean alert = budgetService.checkBudgetUsage(
                user.getUserId(), category.getCategoryId(), transaction.getAmount()
        );
        if (alert) {
            try {
                scheduledEmailService.sendBudgetAlerts();
                System.out.println("⚠️ Budget warning email sent to " + user.getEmail());
            } catch (Exception e) {
                System.err.println("Failed to send budget alert: " + e.getMessage());
            }
        }
        return transactionMapper.toDTO(transaction);
    }

    /**
     * Runs every day at midnight to check and create recurring transactions.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    @Override
    public void processRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> dueTransactions =
                transactionRepository.findByIsRecurringTrueAndNextDueDateBefore(now);

        System.out.println("🕒 Processing " + dueTransactions.size() + " recurring transactions...");

        for (Transaction transaction : dueTransactions) {
            try {
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setUserId(transaction.getUser().getUserId());
                transactionDTO.setAccountId(transaction.getAccount().getAccountId());
                transactionDTO.setAmount(transaction.getAmount());
                transactionDTO.setTransactionType(transaction.getTransactionType().name());
                transactionDTO.setDescription(transaction.getDescription());
                transactionDTO.setRecurring(false); // one-time execution
                transactionDTO.setDateCreated(now);
                transactionDTO.setCategoryId(transaction.getCategory().getCategoryId());
                transactionDTO.setPaymentMethod(transaction.getPaymentMethod().name());
                transactionDTO.setStatus(TransactionStatus.COMPLETED.name());

                Transaction newTransaction = transactionMapper.toEntity(
                        transactionDTO,
                        transaction.getUser(),
                        transaction.getAccount(),
                        transaction.getToAccount() // Keep same toAccount if needed
                );
                newTransaction.setCategory(transaction.getCategory()); // ✅ Manually set it

                transactionRepository.save(newTransaction);
                transaction.setNextDueDate(calculateNextDueDate(transaction));
                transactionRepository.save(transaction);

            } catch (Exception ex) {
                System.err.println("❌ Failed to process recurring transaction ID: " +
                        transaction.getTransactionId() + " → " + ex.getMessage());
            }
        }
    }


    private LocalDateTime calculateNextDueDate(Transaction transaction) {
        switch (transaction.getRecurringInterval()) {
            case DAILY:
                return transaction.getNextDueDate().plusDays(1);
            case WEEKLY:
                return transaction.getNextDueDate().plusWeeks(1);
            case MONTHLY:
                return transaction.getNextDueDate().plusMonths(1);
            case YEARLY:
                return transaction.getNextDueDate().plusYears(1);
            default:
                return transaction.getNextDueDate();
        }
    }

    public TransactionDTO createRecurringTransaction(TransactionDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        RecurringInterval interval = RecurringInterval.valueOf(dto.getRecurringInterval().toUpperCase());

        dto.setRecurring(true);
        dto.setDateCreated(LocalDateTime.now());
        dto.setNextDueDate(LocalDateTime.now().plusDays(1));

        Transaction tx = transactionMapper.toEntity(dto, user, account, null); // 🔥
        tx.setCategory(category); // ✅ Set category here
        tx.setRecurringInterval(interval);

        tx = transactionRepository.save(tx);

        boolean isBudgetExceeded = budgetService.checkBudgetUsage(
                dto.getUserId(),
                dto.getCategoryId(),
                dto.getAmount()
        );
//        if (isBudgetExceeded) {
//            scheduledEmailService.sendBudgetAlerts();
//            System.out.println("⚠️ Budget alert sent to " + user.getEmail());
//        }

        return transactionMapper.toDTO(tx);
    }


}
