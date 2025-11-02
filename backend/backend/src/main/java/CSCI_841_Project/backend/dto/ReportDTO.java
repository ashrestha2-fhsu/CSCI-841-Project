package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.ReportGeneratedBy;
import CSCI_841_Project.backend.enums.ReportFileFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public class ReportDTO {

    /** Unique report ID */
    private Long reportId;
    private Long userId;
    /** Start and end dates for the report */
    private LocalDate startDate;
    private LocalDate endDate;
    /** Financial summary */
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    /** Report metadata */
    private ReportGeneratedBy generatedBy;
    private ReportFileFormat fileFormat;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;

    // Constructor
    public ReportDTO(){}
    public ReportDTO(Long reportId, Long userId, LocalDate startDate, LocalDate endDate, BigDecimal totalIncome, BigDecimal totalExpense,
                     BigDecimal netBalance, ReportGeneratedBy generatedBy, ReportFileFormat fileFormat, LocalDate dateCreated, LocalDate dateUpdated) {
        this.reportId = reportId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netBalance = netBalance;
        this.generatedBy = generatedBy;
        this.fileFormat = fileFormat;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    // Getter and Setter
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long id) {
        this.reportId = reportId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public ReportGeneratedBy getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(ReportGeneratedBy generatedBy) {
        this.generatedBy = generatedBy;
    }

    public ReportFileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(ReportFileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}