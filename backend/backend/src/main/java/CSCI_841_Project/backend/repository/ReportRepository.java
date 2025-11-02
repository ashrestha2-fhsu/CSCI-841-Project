package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser_UserId(Long userId);

    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.startDate >= :startDate AND r.endDate <= :endDate")
    List<Report> findReportsByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}