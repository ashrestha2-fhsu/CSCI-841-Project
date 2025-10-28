package csci812_project.backend.repository;

import csci812_project.backend.entity.SavingsGoal;
import csci812_project.backend.enums.SavingsGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    /** ✅ Find all savings goals for a specific user */
    List<SavingsGoal> findByUser_UserId(Long userId);

    /** ✅ Find all active savings goals */
    List<SavingsGoal> findByStatus(SavingsGoalStatus status);

    /** ✅ Find all savings goals where `autoSave = true` */
    List<SavingsGoal> findByAutoSaveTrue();

    @Query("SELECT COALESCE(SUM(s.currentAmount), 0) FROM SavingsGoal s WHERE s.user.userId = :userId")
    BigDecimal getTotalSavingsByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM SavingsGoal s WHERE s.user.userId = :userId ORDER BY s.dateCreated DESC LIMIT 1")
    Optional<SavingsGoal> findLatestSavingsGoalByUser(Long userId);
}

