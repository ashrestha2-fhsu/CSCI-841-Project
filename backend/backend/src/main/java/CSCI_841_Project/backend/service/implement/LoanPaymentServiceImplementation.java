package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.LoanPaymentDTO;
import CSCI_841_Project.backend.entity.Loan;
import CSCI_841_Project.backend.entity.LoanPayment;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.LoanPaymentMapper;
import CSCI_841_Project.backend.repository.LoanPaymentRepository;
import CSCI_841_Project.backend.repository.LoanRepository;
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
import java.util.List;
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





    // METHOD TO MAKE AN INDIVIDUAL PAYMENT  +++++++++++++++++++++++++++++++++++++++

    @Override
    @Transactional
    public LoanPaymentDTO makePayment(Long loanId, BigDecimal paymentAmount, BigDecimal extraPayment) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        // ✅ Prevent `NullPointerException`
        if (paymentAmount == null) paymentAmount = BigDecimal.ZERO;
        if (extraPayment == null) extraPayment = BigDecimal.ZERO;

        // ✅ Ensure at least one payment type is provided
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0 && extraPayment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("You must provide either a monthly payment or an extra payment!");
        }

        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(loan);
        loanPayment.setUser(loan.getUser());

        if (paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            processMonthlyPayment(loan, paymentAmount, loanPayment);
        }

        if (extraPayment.compareTo(BigDecimal.ZERO) > 0) {
            loanPayment = processExtraPayment(loan, extraPayment);
        }

        loanPaymentRepository.save(loanPayment);
        // ✅ Recalculate Total Amount Paid & Interest Paid
        BigDecimal totalAmountPaid = loanPaymentRepository.findTotalAmountPaidByLoanId(loanId).orElse(BigDecimal.ZERO);
        BigDecimal totalInterestPaid = loanPaymentRepository.findTotalInterestPaidByLoanId(loanId).orElse(BigDecimal.ZERO);
        // ✅ Update the Loan entity with new aggregated values
        loan.setTotalOutstandingBalance(loan.getOutstandingBalance());

        // ✅ Update Loan Entity
        loanPayment.setTotalAmountPaid(totalAmountPaid);
        loanPayment.setInterestPaid(totalInterestPaid);


        loan.updateLoanStatus();
        loanRepository.save(loan);

        return loanPaymentMapper.toDTO(loanPayment);
    }


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

            //scheduledEmailService.sendLoanReminders();
        }
    }

}
