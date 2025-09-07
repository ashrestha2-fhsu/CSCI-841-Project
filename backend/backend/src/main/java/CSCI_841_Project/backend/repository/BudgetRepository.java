package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /** Find budgets by user ID */
    List<Budget> findByUser_UserId(Long userId);

    /** ✅ Find budgets by user ID and category ID */
    List<Budget> findByUser_UserIdAndCategory_CategoryId(Long userId, Long categoryId);

    /** ✅ Calculate the total amount spent within a budget */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.category.id = :categoryId AND t.user.userId = :userId")
    BigDecimal getTotalSpentInBudget(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * ✅ Fetch all budgets for a user within a given date range
     */
    List<Budget> findByUser_UserIdAndStartDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Budget b WHERE b.user.userId = :userId ORDER BY b.dateCreated DESC LIMIT 1")
    Optional<Budget> findLatestBudgetByUser(Long userId);

    /** Find active budgets (not deleted) */
    List<Budget> findByIsDeletedFalse();

    Optional<Budget> findFirstByUser_UserIdAndCategory_CategoryIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsDeletedFalse(
            Long userId, Long categoryId, LocalDate startDate, LocalDate endDate
    );

}
