package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.InvestmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InvestmentHistoryRepository extends JpaRepository<InvestmentHistory, Long> {

    // ✅ Fetch all investment history for a given investment
    List<InvestmentHistory> findByInvestment_InvestmentId(Long investmentId);

    // ✅ Fetch history within a specific date range
    @Query("SELECT ih FROM InvestmentHistory ih WHERE ih.investment.investmentId = :investmentId " +
            "AND ih.recordedAt BETWEEN :startDate AND :endDate")
    List<InvestmentHistory> findByInvestmentAndDateRange(
            @Param("investmentId") Long investmentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT COALESCE(SUM(ih.returnsGenerated), 0) FROM InvestmentHistory ih WHERE ih.investment.user.userId = :userId AND ih.recordedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalInvestmentReturnsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}