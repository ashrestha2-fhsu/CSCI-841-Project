package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Investment;
import CSCI_841_Project.backend.enums.InvestmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUser_UserId(Long userId);

    List<Investment> findByIsDeletedFalse();

    List<Investment> findByInvestmentType(InvestmentType investmentType);

    @Query("SELECT COALESCE(SUM(ih.returnsGenerated), 0) FROM InvestmentHistory ih WHERE ih.investment.user.userId = :userId AND ih.recordedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalInvestmentsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

//    @Query("SELECT SUM(i.amountInvested) FROM Investment i WHERE i.user.userId = :userId")
//    BigDecimal getTotalInvestmentsByUserId(@Param("userId") Long userId);


}