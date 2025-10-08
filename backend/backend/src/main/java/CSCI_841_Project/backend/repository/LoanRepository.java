package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Loan;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUser_UserId(Long userId);

    List<Loan> findByUserAndStatus(User user, LoanStatus status);

    @Query("SELECT COALESCE(SUM(lp.totalAmountPaid), 0) FROM LoanPayment lp WHERE lp.loan.user.userId = :userId AND lp.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalLoanAmountByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(l.outstandingBalance), 0) FROM Loan l WHERE l.user.userId = :userId")
    BigDecimal getOutstandingLoanBalanceByUserId(@Param("userId") Long userId);

    // ✅ Use FUNCTION('DATE_ADD', ...) for MySQL
    @Query(value = "SELECT * FROM loans l WHERE l.due_date <= DATE_ADD(CURDATE(), INTERVAL 3 DAY)", nativeQuery = true)
    List<Loan> findLoansDueSoon();


    // ✅ Find loans by status (ACTIVE, PAID_OFF, DEFAULTED)
    List<Loan> findByStatus(LoanStatus status);

//    // ✅ Count the number of loans a user has
//    long countByUser_UserId(Long userId);
//
//    // ✅ Sum all outstanding balances for a user's loans
//    @Query("SELECT COALESCE(SUM(l.outstandingBalance), 0) FROM Loan l WHERE l.user.userId = :userId")
//    Optional<BigDecimal> getTotalDebtByUser(@Param("userId") Long userId);
//
//    // ✅ Sum of all loan amounts borrowed by user
//    @Query("SELECT COALESCE(SUM(l.amountBorrowed), 0) FROM Loan l WHERE l.user.userId = :userId")
//    Optional<BigDecimal> getTotalLoanBorrowedByUser(@Param("userId") Long userId);

}