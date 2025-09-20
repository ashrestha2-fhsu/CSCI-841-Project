package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.ReportFileFormat;

import java.time.LocalDate;

public class ReportRequestDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReportFileFormat fileFormat;


    public ReportRequestDTO(){}
    public ReportRequestDTO(Long userId, LocalDate startDate, LocalDate endDate, ReportFileFormat fileFormat) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fileFormat = fileFormat;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public ReportFileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(ReportFileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }
}