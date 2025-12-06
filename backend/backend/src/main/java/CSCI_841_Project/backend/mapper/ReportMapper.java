package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.ReportDTO;
import CSCI_841_Project.backend.entity.Report;
import CSCI_841_Project.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    /**
     * ✅ Convert Report Entity → ReportDTO
     */
    public ReportDTO toDTO(Report report) {
        if (report == null) return null;

        ReportDTO dto = new ReportDTO();
        dto.setReportId(report.getReportId());
        dto.setUserId(report.getUser().getUserId());
        dto.setStartDate(report.getStartDate());
        dto.setEndDate(report.getEndDate());
        dto.setTotalIncome(report.getTotalIncome());
        dto.setTotalExpense(report.getTotalExpense());
        dto.setNetBalance(report.getNetBalance());
        dto.setGeneratedBy(report.getGeneratedBy()); // Convert Enum to String
        dto.setFileFormat(report.getFileFormat()); // Convert Enum to String
        dto.setDateCreated(report.getDateCreated());
        dto.setDateUpdated(report.getDateUpdated());

        return dto;
    }

    /**
     * ✅ Convert ReportDTO → Report Entity
     */
    public Report toEntity(ReportDTO dto, User user) {
        if (dto == null) return null;

        Report report = new Report();
        report.setUser(user);
        report.setStartDate(dto.getStartDate());
        report.setEndDate(dto.getEndDate());
        report.setTotalIncome(dto.getTotalIncome());
        report.setTotalExpense(dto.getTotalExpense());
        report.calculateNetBalance();
        report.setGeneratedBy(dto.getGeneratedBy()); // Convert String to Enum
        report.setFileFormat(dto.getFileFormat()); // Convert String to Enum
        report.setDateUpdated(dto.getDateUpdated());

        return report;
    }
}
