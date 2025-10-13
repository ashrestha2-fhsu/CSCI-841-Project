package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Loan;
import CSCI_841_Project.backend.entity.LoanPayment;
import CSCI_841_Project.backend.enums.PaymentMethod;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.LoanPaymentMapper;
import CSCI_841_Project.backend.repository.AccountRepository;
import CSCI_841_Project.backend.repository.LoanPaymentRepository;
import CSCI_841_Project.backend.repository.LoanRepository;
import CSCI_841_Project.backend.service.AccountService;
import CSCI_841_Project.backend.service.EmailService;
import CSCI_841_Project.backend.service.LoanPaymentService;
import CSCI_841_Project.backend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoanPaymentServiceImplementation implements LoanPaymentService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanPaymentRepository loanPaymentRepository;
    @Autowired
    private LoanPaymentMapper loanPaymentMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LoanService loanService;  // ✅ Inject LoanService
    @Autowired
    private  ScheduledEmailService scheduledEmailService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;





    // METHOD TO MAKE AN INDIVIDUAL PAYMENT  +++++++++++++++++++++++++++++++++++++++

    @Override
    @Transactional
    public LoanPaymentDTO makePayment(Long loanId,
                                      BigDecimal paymentAmount,
                                      BigDecimal extraPayment,
                                      String paymentMethod,          // NEW
                                      Long accountId,                // NEW (required if INTERNAL_ACCOUNT)
                                      String externalReference,      // NEW (optional)
                                      LocalDate paymentDate          // NEW (optional; null -> now)
    ) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        // Normalize
        if (paymentAmount == null) paymentAmount = BigDecimal.ZERO;
        if (extraPayment == null)  extraPayment  = BigDecimal.ZERO;

        boolean hasMonthly = paymentAmount.compareTo(BigDecimal.ZERO) > 0;
        boolean hasExtra   = extraPayment.compareTo(BigDecimal.ZERO) > 0;

        // Exactly one
        if (hasMonthly == hasExtra) {
            throw new RuntimeException("Provide either a monthly payment OR an extra payment (not both).");
        }

        // Parse/Default method
        PaymentMethod method;
        try {
            method = (paymentMethod == null || paymentMethod.isBlank())
                    ? PaymentMethod.CASH
                    : PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Unsupported payment method: " + paymentMethod);
        }

        // Resolve account if needed
        // Resolve account if needed (no AccountService.debit, no availableBalance)
        Account account = null;
        if (method == PaymentMethod.INTERNAL_ACCOUNT) {
            if (accountId == null) {
                throw new RuntimeException("accountId is required for INTERNAL_ACCOUNT payments.");
            }
            account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Account not found"));

            // Optional: ensure not soft-deleted (if your Account has isDeleted flag)
             if (account.isDeleted()) throw new RuntimeException("Selected account is deleted.");

            // Optional: ownership check to match the loan owner
             if (!Objects.equals(account.getUser().getUserId(), loan.getUser().getUserId())) {
                 throw new RuntimeException("Account does not belong to this user.");
             }

            BigDecimal debit = hasMonthly ? paymentAmount : extraPayment;

            // Use current balance; no 'availableBalance' in your model
            if (account.getBalance() == null) {
                account.setBalance(BigDecimal.ZERO);
            }
            if (account.getBalance().compareTo(debit) < 0) {
                throw new RuntimeException("Insufficient funds in the selected account.");
            }

            // Subtract and persist
            account.setBalance(account.getBalance().subtract(debit));
            accountRepository.save(account);
        }


        // Build payment record
        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(loan);
        loanPayment.setUser(loan.getUser());
        loanPayment.setPaymentMethod(method);                 // NEW
        loanPayment.setAccount(account);                      // NEW
        loanPayment.setExternalReference(externalReference);  // NEW
        loanPayment.setPaymentDate(
                paymentDate == null ? LocalDateTime.now() : paymentDate.atStartOfDay()
        );

        // Apply math
        if (hasMonthly) {
            processMonthlyPayment(loan, paymentAmount, loanPayment);
        } else {
            // principal-only extra payment, do NOT move due date
            if (extraPayment.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Extra payment must be greater than zero!");
            }
            loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(extraPayment));
            if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) < 0) {
                loan.setOutstandingBalance(BigDecimal.ZERO);
            }

            loanPayment.setPaymentAmount(BigDecimal.ZERO);
            loanPayment.setExtraPayment(extraPayment);
            loanPayment.setPrincipalPaid(extraPayment);
            loanPayment.setInterestPaid(BigDecimal.ZERO);
            loanPayment.setRemainingBalance(loan.getOutstandingBalance());
            loanPayment.setLastPaymentDate(LocalDate.now());
            loanPayment.setNextDueDate(loan.getDueDate());
        }

        // Persist
        loanPaymentRepository.save(loanPayment);

        // Aggregates
        BigDecimal totalAmountPaid   = loanPaymentRepository.findTotalAmountPaidByLoanId(loanId).orElse(BigDecimal.ZERO);
        BigDecimal totalInterestPaid = loanPaymentRepository.findTotalInterestPaidByLoanId(loanId).orElse(BigDecimal.ZERO);

        // Update snapshots
        loan.setTotalOutstandingBalance(loan.getOutstandingBalance());
        loanPayment.setTotalAmountPaid(totalAmountPaid);
        // If you store per-payment interest in the row, DO NOT overwrite it here.
        // If you store cumulative interest in the row, uncomment:
        // loanPayment.setInterestPaid(totalInterestPaid);

        loan.updateLoanStatus();
        loanRepository.save(loan);

        return loanPaymentMapper.toDTO(loanPayment);
    }


//    @Override
//    @Transactional
//    public LoanPaymentDTO makePayment(Long loanId, BigDecimal paymentAmount, BigDecimal extraPayment) {
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new NotFoundException("Loan not found"));
//
//        // ✅ Prevent `NullPointerException`
//        if (paymentAmount == null) paymentAmount = BigDecimal.ZERO;
//        if (extraPayment == null) extraPayment = BigDecimal.ZERO;
//
//        // ✅ Ensure at least one payment type is provided
//        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0 && extraPayment.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new RuntimeException("You must provide either a monthly payment or an extra payment!");
//        }
//
//        LoanPayment loanPayment = new LoanPayment();
//        loanPayment.setLoan(loan);
//        loanPayment.setUser(loan.getUser());
//
//        if (paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
//            processMonthlyPayment(loan, paymentAmount, loanPayment);
//        }
//
//        if (extraPayment.compareTo(BigDecimal.ZERO) > 0) {
//            loanPayment = processExtraPayment(loan, extraPayment);
//        }
//
//        loanPaymentRepository.save(loanPayment);
//        // ✅ Recalculate Total Amount Paid & Interest Paid
//        BigDecimal totalAmountPaid = loanPaymentRepository.findTotalAmountPaidByLoanId(loanId).orElse(BigDecimal.ZERO);
//        BigDecimal totalInterestPaid = loanPaymentRepository.findTotalInterestPaidByLoanId(loanId).orElse(BigDecimal.ZERO);
//        // ✅ Update the Loan entity with new aggregated values
//        loan.setTotalOutstandingBalance(loan.getOutstandingBalance());
//
//        // ✅ Update Loan Entity
//        loanPayment.setTotalAmountPaid(totalAmountPaid);
//        loanPayment.setInterestPaid(totalInterestPaid);
//
//
//        loan.updateLoanStatus();
//        loanRepository.save(loan);
//
//        return loanPaymentMapper.toDTO(loanPayment);
//    }


    /**
     * ✅ Processes a **scheduled monthly payment**.
     * - Deducts the interest & principal from the outstanding balance.
     * - Updates next due date.
     */
    private void processMonthlyPayment(Loan loan, BigDecimal paymentAmount, LoanPayment loanPayment) {
        // ✅ Calculate interest for the current month
        BigDecimal monthlyInterestRate = loan.getInterestRate().divide(BigDecimal.valueOf(100 * 12), RoundingMode.HALF_UP);
        BigDecimal interestForMonth = loan.getOutstandingBalance().multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);

        // ✅ Calculate principal portion
        BigDecimal principalPaid = paymentAmount.subtract(interestForMonth);

        // ✅ Prevent negative principal (in case interest > payment)
        if (principalPaid.compareTo(BigDecimal.ZERO) < 0) {
            principalPaid = BigDecimal.ZERO;
        }

        // ✅ Update Loan Balances
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(principalPaid));

        // ✅ Prevent negative balance
        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) < 0) {
            loan.setOutstandingBalance(BigDecimal.ZERO);
        }

        // ✅ Set Loan Payment Details
        loanPayment.setPaymentAmount(paymentAmount);
        loanPayment.setPrincipalPaid(principalPaid);
        loanPayment.setInterestPaid(interestForMonth);
        loanPayment.setRemainingBalance(loan.getOutstandingBalance());

        // ✅ Set Next Due Date for Regular Payments
        loanPayment.setNextDueDate(LocalDate.now().plusMonths(1));
    }


    /**
     * ✅ Processes an **extra principal-only payment**.
     * - Directly reduces the principal.
     * - Does NOT affect the next due date.
     */
    private LoanPayment processExtraPayment(Loan loan, BigDecimal extraPayment) {
        if (extraPayment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Extra payment must be greater than zero!");
        }

        // ✅ Reduce Principal Directly
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(extraPayment));

        // ✅ Prevent negative balance
        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) < 0) {
            loan.setOutstandingBalance(BigDecimal.ZERO);
        }

        // ✅ Save Loan Payment Record using `recordLoanPayment`
        return recordLoanPayment(loan, BigDecimal.ZERO, extraPayment, BigDecimal.ZERO, extraPayment);
    }


    /**
     * ✅ Saves the loan payment record.
     */
    private LoanPayment recordLoanPayment(Loan loan, BigDecimal paymentAmount, BigDecimal principalPaid, BigDecimal interestPaid, BigDecimal extraPayment) {
        LoanPayment payment = new LoanPayment();
        payment.setLoan(loan);
        payment.setUser(loan.getUser());
        payment.setPaymentAmount(paymentAmount);
        payment.setPrincipalPaid(principalPaid);
        payment.setInterestPaid(interestPaid);
        payment.setExtraPayment(extraPayment);
        payment.setRemainingBalance(loan.getOutstandingBalance());
        payment.setLastPaymentDate(LocalDate.now());
        payment.setNextDueDate(loan.getDueDate());

        return loanPaymentRepository.save(payment);
    }



    @Override
    @Transactional(readOnly = true)
    public List<LoanPaymentDTO> getPaymentsByLoan(Long loanId) {
        return loanPaymentRepository.findByLoan_LoanId(loanId)
                .stream()
                .map(loanPaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Run every day at 12 AM (Midnight) to check for upcoming payments
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs every midnight
    public void sendLoanPaymentReminders() {
        LocalDate reminderDate = LocalDate.now().plusDays(3); // Notify 3 days before due date
        List<LoanPayment> upcomingPayments = loanPaymentRepository.findByNextDueDate(reminderDate);

        for (LoanPayment payment : upcomingPayments) {
            String userEmail = payment.getUser().getEmail();
            String loanName = payment.getLoan().getLenderName();
            BigDecimal amountDue = payment.getPaymentAmount();
            String dueDate = payment.getNextDueDate().toString();

            scheduledEmailService.sendLoanReminders();
        }
    }

}

