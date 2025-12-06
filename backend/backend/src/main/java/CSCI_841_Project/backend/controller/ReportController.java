package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.*;
import CSCI_841_Project.backend.enums.ReportFileFormat;
import CSCI_841_Project.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;


    /**
     * ✅ Generate a financial report for a user.
     */
    @PostMapping("/generate")
    public ResponseEntity<ReportDTO> generateReport(@RequestBody ReportRequestDTO requestDTO) {
        ReportDTO report = reportService.generateReport(
                requestDTO.getUserId(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getFileFormat());

        return ResponseEntity.ok(report);
    }

    /**
     * ✅ Retrieve all reports for a user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportDTO>> getUserReports(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getReportsByUser(userId));
    }

    /**
     * ✅ Manually triggers the monthly report generation for all users.
     * Only Admins should have access to this.
     */
    @PostMapping("/generate-monthly")
    public ResponseEntity<String> generateMonthlyReports() {
        reportService.generateMonthlyReports();
        return ResponseEntity.ok("✅ Monthly reports generated manually.");
    }

    /** ✅ Generate Loan Report */
    @GetMapping("/user/{userId}/loans")
    public ResponseEntity<LoanReportDTO> generateLoanReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateLoanReport(userId, LocalDate.now().minusMonths(1), LocalDate.now()));
    }

    /** ✅ Generate Investment Report */
    @GetMapping("/user/{userId}/investments")
    public ResponseEntity<InvestmentReportDTO> generateInvestmentReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateInvestmentReport(userId, LocalDate.now().minusMonths(1), LocalDate.now()));
    }

    /** ✅ Generate Savings Goal Report */
    // ✅ Savings Goal Report Endpoint
    @GetMapping("/user/{userId}/")
    public ResponseEntity<SavingsGoalReportDTO> generateSavingsGoalReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateSavingsGoalReport(userId, LocalDate.now().minusMonths(1), LocalDate.now()));
    }

    @GetMapping("/user/{userId}/budgets")
    public ResponseEntity<BudgetReportDTO> generateBudgetReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateBudgetReport(userId, LocalDate.now().minusMonths(1), LocalDate.now()));
    }


    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<TransactionReportDTO> generateTransactionReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateTransactionReport(userId, LocalDate.now().minusMonths(1), LocalDate.now()));
    }

    @GetMapping("/user/{userId}/reports")
    public ResponseEntity<List<ReportDTO>> getReports(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getReportsByUserAndDate(userId, LocalDate.now().minusMonths(1), LocalDate.now()));
    }


    // BUILD A REST API TO DOWNLOAD REPORT
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam Long reportId, @RequestParam ReportFileFormat format) {
        try {
            // ✅ Determine correct file extension
            String fileExtension = switch (format) {
                case PDF -> ".pdf";
                case CSV -> ".csv";
                case EXCEL -> ".xlsx";
            };

            // ✅ Construct the file path
            Path filePath = Paths.get("reports/report_" + reportId + fileExtension);
            File file = filePath.toFile();

            // ✅ Check if the file exists
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // ✅ Load file as resource
            Resource resource = new UrlResource(filePath.toUri());

            // ✅ Set correct Content-Type
            String contentType = switch (format) {
                case PDF -> "application/pdf";
                case CSV -> "text/csv";
                case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            };

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}