package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.LoanDTO;
import CSCI_841_Project.backend.entity.Loan;
import CSCI_841_Project.backend.entity.LoanPayment;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.LoanStatus;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.LoanMapper;
import CSCI_841_Project.backend.repository.LoanPaymentRepository;
import CSCI_841_Project.backend.repository.LoanRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImplementation implements LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoanMapper loanMapper;
    @Autowired
    private LoanPaymentRepository loanPaymentRepository;


    // METHOD TO CREATE NEW LOAN  +++++++++++++++++++++++++++++++++++++++++++
    /**
     * ✅ Creates a new loan and initializes default values.
     */
    @Override
    @Transactional
    public LoanDTO createLoan(LoanDTO loanDTO) {
        User user = userRepository.findById(loanDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Loan loan = loanMapper.toEntity(loanDTO, user);

        // ✅ Validate Loan Term (Must be at least 1 year)
        if (loan.getNumberOfYears() < 1) {
            throw new RuntimeException("Loan term must be at least 1 year!");
        }

        // ✅ Prevent `NullPointerException` by setting default values
        if (loan.getNumberOfLoans() == null) loan.setNumberOfLoans(0);
        if (loan.getTotalLoanBorrowed() == null) loan.setTotalLoanBorrowed(BigDecimal.ZERO);
        if (loan.getTotalOutstandingBalance() == null) loan.setTotalOutstandingBalance(BigDecimal.ZERO);

        // ✅ Calculate Monthly Payment
        loan.calculateMonthlyPayment();
        if (loan.getMonthlyPayment() == null) {
            throw new RuntimeException("Monthly payment calculation failed!");
        }

        // ✅ Set Initial Loan Values
        loan.setOutstandingBalance(loan.getAmountBorrowed());  // Initial balance = borrowed amount
        loan.updateNextDueDate();  // Set first due date
        loan.updateLoanStatus();   // Set loan status

        // ✅ Update Loan Aggregates inside Loan Entity
        loan.setNumberOfLoans(loan.getNumberOfLoans() + 1);
        loan.setTotalLoanBorrowed(loan.getTotalLoanBorrowed().add(loan.getAmountBorrowed()));
        loan.setTotalOutstandingBalance(loan.getTotalOutstandingBalance().add(loan.getOutstandingBalance()));

        // ✅ Save Loan
        loan = loanRepository.save(loan);

        // ✅ Create Initial LoanPayment Entry (Tracks first loan payment cycle)
        LoanPayment firstPayment = new LoanPayment();
        firstPayment.setLoan(loan);
        firstPayment.setUser(user);
        firstPayment.setPaymentAmount(BigDecimal.ZERO); // No payment yet
        firstPayment.setPrincipalPaid(BigDecimal.ZERO);
        firstPayment.setInterestPaid(BigDecimal.ZERO);
        firstPayment.setExtraPayment(BigDecimal.ZERO);
        firstPayment.setRemainingBalance(loan.getOutstandingBalance());
        firstPayment.setNextDueDate(loan.getDueDate());

        // ✅ Save Initial Payment Record
        loanPaymentRepository.save(firstPayment);

        // ✅ Fix: Pass the User to updateLoanStats()
        updateLoanStats(user);

        return loanMapper.toDTO(loan);
    }

    /**
     * ✅ Updates loan statistics inside the Loan entity.
     * - Tracks the total number of loans.
     * - Calculates the total loan amount borrowed.
     * - Calculates the total outstanding balance.
     */
    @Override
    public void updateLoanStats(User user) {
        List<Loan> userLoans = loanRepository.findByUserAndStatus(user, LoanStatus.ACTIVE);

        // ✅ Aggregate Data Per User
        int totalLoans = userLoans.size();
        BigDecimal totalBorrowed = userLoans.stream()
                .map(Loan::getAmountBorrowed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOutstanding = userLoans.stream()
                .map(Loan::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Set Values Directly in Loan Entity
        for (Loan loan : userLoans) {
            loan.setNumberOfLoans(totalLoans);
            loan.setTotalLoanBorrowed(totalBorrowed);
            loan.setTotalOutstandingBalance(totalOutstanding);
            loanRepository.save(loan);
        }
    }



    // METHOD TO UPDATE LOAN  ++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * ✅ Updates loan details (Only before first payment).
     */
    @Override
    @Transactional
    public LoanDTO updateLoan(Long loanId, LoanDTO loanDTO) {
        Loan existingLoan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        // ✅ Validate Loan Term
        if (loanDTO.getNumberOfYears() < 1) {
            throw new RuntimeException("Loan term must be at least 1 year!");
        }

        // ✅ Prevent Null Values Before Performing Calculations
        if (existingLoan.getTotalLoanBorrowed() == null) {
            existingLoan.setTotalLoanBorrowed(BigDecimal.ZERO);
        }
        if (existingLoan.getTotalOutstandingBalance() == null) {
            existingLoan.setTotalOutstandingBalance(BigDecimal.ZERO);
        }

        // ✅ Update Loan Details
        existingLoan.setLenderName(loanDTO.getLenderName());
        existingLoan.setAmountBorrowed(loanDTO.getAmountBorrowed());
        existingLoan.setNumberOfYears(loanDTO.getNumberOfYears());
        existingLoan.setInterestRate(loanDTO.getInterestRate());
        existingLoan.calculateMonthlyPayment();
        existingLoan.updateNextDueDate();
        existingLoan.updateLoanStatus();

        // ✅ Update Loan Aggregates
        existingLoan.setTotalLoanBorrowed(existingLoan.getTotalLoanBorrowed().add(existingLoan.getAmountBorrowed()));
        existingLoan.setTotalOutstandingBalance(existingLoan.getTotalOutstandingBalance().add(existingLoan.getOutstandingBalance()));

        // ✅ Save Loan
        existingLoan = loanRepository.save(existingLoan);

        // ✅ Update Loan Stats for User
        updateLoanStats(existingLoan.getUser());

        return loanMapper.toDTO(existingLoan);
    }


    /**
     * ✅ Updates loan status based on the remaining balance.
     */
    @Override
    public void updateLoanStatus(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.PAID_OFF);
        } else if (loan.getDueDate().isBefore(LocalDate.now().minusDays(60))) {
            loan.setStatus(LoanStatus.DEFAULTED);
        } else {
            loan.setStatus(LoanStatus.ACTIVE);
        }

        loanRepository.save(loan);
    }

    /**
     * ✅ Updates the next due date (e.g., moves to next month).
     */
    @Override
    public void updateNextDueDate(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getDueDate() == null) {
            loan.setDueDate(LocalDate.now().plusMonths(1)); // ✅ Set first due date
        } else {
            loan.setDueDate(loan.getDueDate().plusMonths(1)); // ✅ Move to next month
        }

        loanRepository.save(loan);
    }

    // METHOD TO GET LOAN BY ID   +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    @Transactional(readOnly = true)
    public LoanDTO getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        return loanMapper.toDTO(loan);
    }

    // METHOD TO GET LOAN BY USER   ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    @Transactional(readOnly = true)
    public List<LoanDTO> getLoansByUser(Long userId) {
        return loanRepository.findByUser_UserId(userId)
                .stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());
    }


    // METHOD TO DELETE LOAN   ++++++++++++++++++++++++++++++++++++++++++
    @Override
    @Transactional
    public void deleteLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        loanRepository.delete(loan);
    }


}