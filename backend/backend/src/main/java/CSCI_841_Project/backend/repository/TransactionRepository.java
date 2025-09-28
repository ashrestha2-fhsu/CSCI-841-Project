package CSCI_841_Project.backend.repository;


import CSCI_841_Project.backend.entity.Transaction;
import CSCI_841_Project.backend.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** Find all transactions by user ID */
    List<Transaction> findByUser_UserId(Long userId);

    /** Find all transactions for a specific account */
    List<Transaction> findByAccount_AccountId(Long accountId);

    /** Find all transactions by transaction type */
    List<Transaction> findByTransactionType(TransactionType transactionType);

    /** Find recurring transactions that are due */
    List<Transaction> findByIsRecurringTrueAndNextDueDateBefore(LocalDateTime now);


    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.userId = :userId AND t.transactionType = 'INCOME' AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalIncome(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.userId = :userId AND t.transactionType = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpense(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * ✅ Fetch transactions for a user within a given date range
     */
    List<Transaction> findByUser_UserIdAndDateBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    // Add Method to Sum Expenses
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.userId = :userId " +
            "AND t.category.categoryId = :categoryId " +
            "AND t.date BETWEEN :start AND :end " +
            "AND t.transactionType = 'EXPENSE' " +
            "AND t.status IN ('COMPLETED', 'PENDING') " +
            "AND t.isDeleted = false")
    BigDecimal sumExpensesInCategoryForPeriod(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


}
