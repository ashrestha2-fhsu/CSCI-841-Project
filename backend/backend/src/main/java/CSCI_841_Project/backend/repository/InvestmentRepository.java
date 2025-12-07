package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Investment;
import CSCI_841_Project.backend.enums.InvestmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUser_UserId(Long userId);

    Page<Investment> findByUser_UserId(Long userId, Pageable pageable);



    @Query("SELECT COALESCE(SUM(ih.returnsGenerated), 0) FROM InvestmentHistory ih WHERE ih.investment.user.userId = :userId AND ih.recordedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalInvestmentsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    Optional<Investment> findByUser_UserIdAndAssetSymbol(Long userId, String assetSymbol);

    @Modifying
    @Query("UPDATE Investment i SET i.isDeleted = true WHERE i.investmentId = :id")
    void softDeleteById(@Param("id") Long id);

    @Query("SELECT i FROM Investment i WHERE i.user.userId = :userId AND i.purchaseDate BETWEEN :startDate AND :endDate AND i.isDeleted = false")
    List<Investment> findInvestmentsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}