package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {

    List<LoanPayment> findByLoan_LoanId(Long loanId);

    List<LoanPayment> findByNextDueDate(LocalDate nextDueDate);

    @Query("SELECT COALESCE(SUM(lp.paymentAmount + lp.extraPayment), 0) FROM LoanPayment lp WHERE lp.loan.loanId = :loanId")
    Optional<BigDecimal> findTotalAmountPaidByLoanId(@Param("loanId") Long loanId);

    @Query("SELECT COALESCE(SUM(lp.interestPaid), 0) FROM LoanPayment lp WHERE lp.loan.loanId = :loanId")
    Optional<BigDecimal> findTotalInterestPaidByLoanId(@Param("loanId") Long loanId);

    @Query("SELECT COALESCE(SUM(lp.totalAmountPaid), 0) FROM LoanPayment lp " +
            "WHERE lp.user.userId = :userId " +
            "AND lp.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalLoanPaymentsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}