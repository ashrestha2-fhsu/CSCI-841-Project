package CSCI_841_Project.backend.service.implement;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import CSCI_841_Project.backend.dto.*;
import CSCI_841_Project.backend.entity.*;
import CSCI_841_Project.backend.enums.ReportFileFormat;
import CSCI_841_Project.backend.enums.ReportGeneratedBy;
import CSCI_841_Project.backend.enums.TransactionType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.ReportMapper;
import CSCI_841_Project.backend.repository.*;
import CSCI_841_Project.backend.service.ReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.Cell; // ✅ Correct - This is for Excel
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReportServiceImplementation implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImplementation.class);

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanPaymentRepository loanPaymentRepository;
    @Autowired
    private InvestmentHistoryRepository investmentHistoryRepository;
    @Autowired
    private BudgetRepository budgetRepository;



    @Override
    @Transactional
    public ReportDTO generateReport(Long userId, LocalDate startDate, LocalDate endDate, ReportFileFormat format) {
        logger.info("📊 Generating report for User ID: {}, Start Date: {}, End Date: {}", userId, startDate, endDate);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("❌ User not found for ID: {}", userId);
                    return new NotFoundException("User not found");
                });

        if (startDate.isAfter(endDate)) {
            logger.warn("⚠️ Invalid date range: Start Date {} is after End Date {}", startDate, endDate);
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        if (format == null) {
            logger.error("❌ Report format is missing!");
            throw new IllegalArgumentException("Report format cannot be null.");
        }
        // ✅ Convert `LocalDate` to `LocalDateTime`
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        // ✅ Fetch Financial Data with Logging
        logger.info("📊 Fetching Total Income...");
        BigDecimal totalIncome = Objects.requireNonNullElse(
                transactionRepository.calculateTotalIncome(userId, startDateTime, endDateTime),
                BigDecimal.ZERO
        );
        logger.info("✅ Retrieved Total Income: {}", totalIncome);
        logger.info("📊 Fetching Total Expense...");
        BigDecimal totalExpense = Objects.requireNonNullElse(
                transactionRepository.calculateTotalExpense(userId, startDateTime, endDateTime),
                BigDecimal.ZERO
        );
        logger.info("✅ Retrieved Total Expense: {}", totalExpense);

        BigDecimal totalSavings = Objects.requireNonNullElse(savingsGoalRepository
                .getTotalSavingsByUserId(userId), BigDecimal.ZERO);
        // ✅ Pass startDateTime and endDateTime to repository
        BigDecimal totalLoans = Objects.requireNonNullElse(
                loanRepository.getTotalLoanAmountByUserId(userId, startDateTime, endDateTime),  // ✅ Now passing all required arguments
                BigDecimal.ZERO
        );
        BigDecimal outstandingDebt = Objects.requireNonNullElse(
                loanRepository.getOutstandingLoanBalanceByUserId(userId),
                BigDecimal.ZERO
        );
        BigDecimal totalInvestments = Objects.requireNonNullElse(
                investmentRepository.getTotalInvestmentsByUserId(userId, startDateTime, endDateTime),  // ✅ Now passing all required arguments
                BigDecimal.ZERO
        );
        BigDecimal totalInvestmentReturns = Objects.requireNonNullElse(
                investmentHistoryRepository.getTotalInvestmentReturnsByUserId(userId, startDateTime, endDateTime),
                BigDecimal.ZERO
        );
        BigDecimal totalBalance = Objects.requireNonNullElse(accountRepository
                .getTotalAccountBalanceByUserId(userId), BigDecimal.ZERO);
        // ✅ Log the retrieved financial data
        logger.info("🔍 Financial Data: Total Savings={}, Total Loans={}, Outstanding Debt={}, Total Investments={}, Investment Returns={}",
                totalSavings, totalLoans, outstandingDebt, totalInvestments, totalInvestmentReturns);
        logger.info("🔍 Total Balance in Accounts: {}", totalBalance);

        // ✅ Compute Net Worth: (Account Balance + Investments + Savings - Expenses - Loans)
        BigDecimal netWorth = totalBalance
                .add(totalInvestments)
                .add(totalSavings)
                .subtract(totalExpense)
                .subtract(outstandingDebt);
        logger.info("📊 Computed Net Worth: {}", netWorth);

        // ✅ Create and Save Report
        Report report = new Report();
        report.setUser(user);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setNetBalance(netWorth); // ✅ Uses net worth calculation
        report.setFileFormat(format);
        report.setGeneratedBy(ReportGeneratedBy.SYSTEM);

        reportRepository.save(report);
        logger.info("✅ Report successfully saved for User ID: {}", userId);

        // ✅ Export the report based on requested format
        if (format == ReportFileFormat.CSV) {
            exportCsvReport(report);
        } else if (format == ReportFileFormat.EXCEL) {
            exportExcelReport(report);
        } else {
            exportPdfReport(report);
        }
        return reportMapper.toDTO(report);
    }


    @Override
    public LoanReportDTO generateLoanReport(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        // ✅ Fetch all loans for the user
        List<Loan> loans = loanRepository.findByUser_UserId(userId);
        // ✅ Compute Total Loans Data
        int numberOfLoans = loans.size();
        BigDecimal totalLoanBorrowed = loans.stream()
                .map(Loan::getAmountBorrowed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOutstandingBalance = loans.stream()
                .map(Loan::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // ✅ Fetch Loan Payments within Date Range
        BigDecimal totalAmountPaid = Objects.requireNonNullElse(
                loanPaymentRepository.getTotalLoanPaymentsByUserId(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)),
                BigDecimal.ZERO
        );
        logger.info("📊 Loan Report - User ID: {}, Total Loans: {}, Total Borrowed: {}, Total Outstanding: {}, Total Paid: {}",
                userId, numberOfLoans, totalLoanBorrowed, totalOutstandingBalance, totalAmountPaid);
        // ✅ Create a Loan Report DTO (Instead of using a generic ReportDTO)
        LoanReportDTO loanReport = new LoanReportDTO();
        loanReport.setUserId(userId);
        loanReport.setStartDate(startDate);
        loanReport.setEndDate(endDate);
        loanReport.setNumberOfLoans(numberOfLoans);
        loanReport.setTotalLoanBorrowed(totalLoanBorrowed);
        loanReport.setTotalOutstandingBalance(totalOutstandingBalance);
        loanReport.setTotalAmountPaid(totalAmountPaid);

        // ✅ Add individual loan details
        List<LoanDetailsDTO> loanDetails = loans.stream().map(loan -> {
            LoanDetailsDTO dto = new LoanDetailsDTO();
            dto.setLenderName(loan.getLenderName());
            dto.setAmountBorrowed(loan.getAmountBorrowed());
            dto.setOutstandingBalance(loan.getOutstandingBalance());
            dto.setMonthlyPayment(loan.getMonthlyPayment());
            return dto;
        }).collect(Collectors.toList());

        loanReport.setLoans(loanDetails);
        return loanReport;
    }


    @Override
    @Transactional
    public InvestmentReportDTO generateInvestmentReport(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // ✅ Fetch All Investments for the User
        List<Investment> investments = investmentRepository.findByUser_UserId(userId);
        // ✅ Compute Total Investment Data
        BigDecimal totalInvested = investments.stream()
                .map(Investment::getAmountInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCurrentValue = investments.stream()
                .map(Investment::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // ✅ Fetch Investment Returns from `InvestmentHistory`
        BigDecimal totalReturns = Objects.requireNonNullElse(
                investmentHistoryRepository.getTotalInvestmentReturnsByUserId(userId, startDateTime, endDateTime),
                BigDecimal.ZERO
        );
        logger.info("📊 Investment Report - User ID: {}, Total Invested: {}, Current Value: {}, Total Returns: {}",
                userId, totalInvested, totalCurrentValue, totalReturns);
        // ✅ Create Investment Report DTO
        InvestmentReportDTO investmentReport = new InvestmentReportDTO();
        investmentReport.setUserId(userId);
        investmentReport.setStartDate(startDate);
        investmentReport.setEndDate(endDate);
        investmentReport.setTotalInvested(totalInvested);
        investmentReport.setTotalCurrentValue(totalCurrentValue);
        investmentReport.setTotalReturns(totalReturns);

        // ✅ Add Individual Investment Details
        List<InvestmentDetailsDTO> investmentDetails = investments.stream().map(investment -> {
            InvestmentDetailsDTO dto = new InvestmentDetailsDTO();
            dto.setInvestmentType(investment.getInvestmentType());
            dto.setAssetName(investment.getAssetName());
            dto.setAmountInvested(investment.getAmountInvested());
            dto.setCurrentValue(investment.getCurrentValue());
            dto.setPurchaseDate(investment.getPurchaseDate().toLocalDate()); // ✅ Convert LocalDateTime → LocalDate
            dto.setLastUpdated(investment.getLastUpdated());
            dto.setPerformance(investment.getPerformance());
            return dto;
        }).collect(Collectors.toList());

        investmentReport.setInvestments(investmentDetails);
        return investmentReport;
    }


    @Override
    @Transactional
    public SavingsGoalReportDTO generateSavingsGoalReport(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        // ✅ Fetch all savings goals for the user
        List<SavingsGoal> goals = savingsGoalRepository.findByUser_UserId(userId);

        // ✅ Compute Total Savings Data
        BigDecimal totalSaved = goals.stream()
                .map(SavingsGoal::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTarget = goals.stream()
                .map(SavingsGoal::getTargetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // ✅ Calculate progress percentage
        BigDecimal progress = totalTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalSaved.divide(totalTarget, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        logger.info("📊 Savings Goal Report - User ID: {}, Total Saved: {}, Total Target: {}, Progress: {}%",
                userId, totalSaved, totalTarget, progress);

        // ✅ Create Savings Goal Report DTO
        SavingsGoalReportDTO savingsReport = new SavingsGoalReportDTO();
        savingsReport.setUserId(userId);
        savingsReport.setStartDate(startDate);
        savingsReport.setEndDate(endDate);
        savingsReport.setTotalSaved(totalSaved);
        savingsReport.setTotalTarget(totalTarget);
        savingsReport.setProgress(progress);

        // ✅ Add Individual Savings Goal Details
        List<SavingsGoalDetailsDTO> savingsDetails = goals.stream().map(goal -> {
            SavingsGoalDetailsDTO dto = new SavingsGoalDetailsDTO();
            dto.setGoalName(goal.getGoalName());
            dto.setTargetAmount(goal.getTargetAmount());
            dto.setCurrentAmount(goal.getCurrentAmount());
            dto.setDeadline(goal.getDeadline());
            dto.setStatus(goal.getStatus());
            dto.setPriorityLevel(goal.getPriorityLevel());
            dto.setContributionFrequency(goal.getContributionFrequency());
            dto.setDateUpdated(goal.getDateUpdated().atStartOfDay()); // ✅ Convert LocalDate → LocalDateTime

            return dto;

        }).collect(Collectors.toList());

        savingsReport.setSavingsGoals(savingsDetails);
        return savingsReport;
    }


    @Override
    @Transactional
    public BudgetReportDTO generateBudgetReport(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        // ✅ Fetch all budgets for the user
        List<Budget> budgets = budgetRepository.findByUser_UserIdAndStartDateBetween(userId, startDate, endDate);
        // ✅ Compute total budget and rollover amount safely
        BigDecimal totalBudgetLimit = budgets.stream()
                .map(budget -> Objects.requireNonNullElse(budget.getAmountLimit(), BigDecimal.ZERO)) // ✅ Prevent null
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRolloverAmount = budgets.stream()
                .map(budget -> Objects.requireNonNullElse(budget.getRolloverAmount(), BigDecimal.ZERO)) // ✅ Prevent null
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("📊 Budget Report - User ID: {}, Total Budget Limit: {}, Total Rollover Amount: {}",
                userId, totalBudgetLimit, totalRolloverAmount);

        // ✅ Create Budget Report DTO
        BudgetReportDTO budgetReport = new BudgetReportDTO();
        budgetReport.setUserId(userId);
        budgetReport.setStartDate(startDate);
        budgetReport.setEndDate(endDate);
        budgetReport.setTotalBudgetLimit(totalBudgetLimit);
        budgetReport.setTotalRolloverAmount(totalRolloverAmount);

        // ✅ Add Individual Budget Details
        List<BudgetDetailsDTO> budgetDetails = budgets.stream().map(budget -> {
            BudgetDetailsDTO dto = new BudgetDetailsDTO();
            dto.setDescription(budget.getDescription());
            dto.setAmountLimit(budget.getAmountLimit());
            dto.setStartDate(budget.getStartDate());
            dto.setEndDate(budget.getEndDate());
            dto.setBudgetType(budget.getBudgetType().name()); // Enum to String
            dto.setRolloverAmount(budget.getRolloverAmount());
            dto.setDateCreated(budget.getDateCreated().atStartOfDay());
            dto.setCategory(budget.getCategory().getName()); // Fetch category name
            return dto;
        }).collect(Collectors.toList());

        budgetReport.setBudgets(budgetDetails);
        return budgetReport;
    }


    @Override
    @Transactional
    public TransactionReportDTO generateTransactionReport(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // ✅ Fetch Transactions within Date Range
        List<Transaction> transactions = transactionRepository.findByUser_UserIdAndDateBetween(
                userId, startDateTime, endDateTime);
        // ✅ Compute Total Income and Total Expense
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getTransactionType().name().equalsIgnoreCase("INCOME")) // ✅ Fix for enum
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getTransactionType().name().equalsIgnoreCase("EXPENSE")) // ✅ Fix for enum
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("📊 Transaction Report - User ID: {}, Total Income: {}, Total Expense: {}",
                userId, totalIncome, totalExpense);

        // ✅ Create Transaction Report DTO
        TransactionReportDTO transactionReport = new TransactionReportDTO();
        transactionReport.setUserId(userId);
        transactionReport.setStartDate(startDate);
        transactionReport.setEndDate(endDate);
        transactionReport.setTotalIncome(totalIncome);
        transactionReport.setTotalExpense(totalExpense);

        // ✅ Add Individual Transaction Details
        List<TransactionDetailsDTO> transactionDetails = transactions.stream().map(transaction -> {
            TransactionDetailsDTO dto = new TransactionDetailsDTO();
            dto.setAmount(transaction.getAmount());
            dto.setTransactionType(TransactionType.valueOf(transaction.getTransactionType().name()));
//            dto.setTransactionType(transaction.getTransactionType());
            dto.setDescription(transaction.getDescription());
            dto.setDate(transaction.getDate());
            dto.setPaymentMethod(transaction.getPaymentMethod());
            dto.setNextDueDate(transaction.getNextDueDate() != null ? transaction.getNextDueDate().toLocalDate() : null); // ✅ Fix type mismatch
            dto.setToAccountId(transaction.getAccount().getAccountId());
            dto.setDateCreated(transaction.getDateCreated());
            dto.setStatus(transaction.getStatus());
            dto.setAccountName(transaction.getAccount().getName()); // ✅ Fetch from Account entity
            dto.setCategory(transaction.getCategory() != null ? transaction.getCategory().getName() : "Uncategorized");

//            dto.setCategory(transaction.getCategory().getName()); // ✅ Fetch from Category entity
            return dto;
        }).collect(Collectors.toList());

        transactionReport.setTransactions(transactionDetails);
        return transactionReport;
    }


    /**
     * ✅ Runs automatically on the 1st day of every month at midnight.
     * Generates reports for all users for the previous month.
     */
    @Scheduled(cron = "0 0 0 1 * ?") // Runs at 12:00 AM on the 1st of every month
    @Transactional
    @Override
    public void generateMonthlyReports() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());

        List<User> users = userRepository.findAll();

        // ✅ Use parallel processing, but ensure transaction safety
        users.parallelStream().forEach(user -> {
            try {
                generateReportForUser(user.getUserId(), startDate, endDate);
            } catch (Exception e) {
                System.err.println("⚠️ Error generating report for User ID: " + user.getUserId() + " - " + e.getMessage());
            }
        });

        System.out.println("✅ Monthly financial reports generated for all users!");
    }

    @Override
    public List<ReportDTO> getReportsByUser(Long userId) {
        return reportRepository.findByUser_UserId(userId)
                .stream()
                .map(reportMapper::toDTO)
                .toList();
    }

    /**
     * ✅ Generate a report for a specific user within a given date range.
     */
    private void generateReportForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        // ✅ Convert `LocalDate` to `LocalDateTime`
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Start of the day
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // End of the day

        // ✅ Fetch financial data with correct date types
        BigDecimal totalIncome = transactionRepository.calculateTotalIncome(userId, startDateTime, endDateTime);
        BigDecimal totalExpense = transactionRepository.calculateTotalExpense(userId, startDateTime, endDateTime);

        // ✅ Create Report
        Report report = new Report();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        report.setUser(user); // ✅ Fetch user from the database

        report.setStartDate(startDate);  // Keep LocalDate in the report entity
        report.setEndDate(endDate);
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.calculateNetBalance(); // ✅ Auto-update balance
        report.setFileFormat(ReportFileFormat.PDF);
        report.setGeneratedBy(ReportGeneratedBy.SYSTEM);

        // ✅ Save Report
        reportRepository.save(report);
        System.out.println("📊 Report generated for User ID: " + userId);
    }


    @Override
    public List<ReportDTO> getReportsByUserAndDate(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Report> reports = reportRepository.findReportsByDateRange(userId, startDate, endDate);
        return reports.stream().map(reportMapper::toDTO).collect(Collectors.toList());
    }


    private void exportPdfReport(Report report) {
        String fileName = "reports/report_" + report.getReportId() + ".pdf";

        try {
            // ✅ Ensure `reports/` directory exists
            File directory = new File("reports");
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("❌ Failed to create reports directory.");
                return;
            }

            PdfWriter writer = new PdfWriter(new FileOutputStream(fileName));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // ✅ Add Title
            document.add(new Paragraph("Financial Report").setBold().setFontSize(16));

            // ✅ Add Report Details
            document.add(new Paragraph("User ID: " + report.getUser().getUserId()));
            document.add(new Paragraph("Start Date: " + report.getStartDate()));
            document.add(new Paragraph("End Date: " + report.getEndDate()));
            document.add(new Paragraph("Total Income: $" + report.getTotalIncome()));
            document.add(new Paragraph("Total Expense: $" + report.getTotalExpense()));
            document.add(new Paragraph("Net Balance: $" + report.getNetBalance()));

            document.close();
            logger.info("✅ PDF report successfully generated: {}", fileName);
        } catch (IOException e) {
            logger.error("❌ Error generating PDF report: {}", e.getMessage());
        }
    }

    private void exportCsvReport(Report report) {
        String fileName = "reports/report_" + report.getReportId() + ".csv";

        try {
            // ✅ Ensure `reports/` directory exists
            File directory = new File("reports");
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("❌ Failed to create reports directory.");
                return;
            }

            try (FileWriter writer = new FileWriter(fileName)) {
                // ✅ Add header row
                writer.append("User ID,Start Date,End Date,Total Income,Total Expense,Net Balance\n");

                // ✅ Add report data
                writer.append(report.getUser().getUserId() + ",")
                        .append(report.getStartDate() + ",")
                        .append(report.getEndDate() + ",")
                        .append(report.getTotalIncome() + ",")
                        .append(report.getTotalExpense() + ",")
                        .append(report.getNetBalance() + "\n");

                logger.info("✅ CSV report successfully generated: {}", fileName);
            }
        } catch (IOException e) {
            logger.error("❌ Error generating CSV report: {}", e.getMessage());
        }
    }


    private File exportExcelReport(Report report) {
        String fileName = "reports/report_" + report.getReportId() + ".xlsx";
        File file = new File(fileName);

        try {
            // ✅ Ensure `reports/` directory exists
            File directory = new File("reports");
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("❌ Failed to create reports directory.");
                return null;
            }

            // ✅ Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Report");

            // ✅ Add header row with styles
            Row headerRow = sheet.createRow(0);
            String[] columns = {"User ID", "Start Date", "End Date", "Total Income", "Total Expense", "Net Balance"};

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // ✅ Add report data
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(report.getUser().getUserId());
            row.createCell(1).setCellValue(report.getStartDate().toString());
            row.createCell(2).setCellValue(report.getEndDate().toString());
            row.createCell(3).setCellValue(report.getTotalIncome().doubleValue());
            row.createCell(4).setCellValue(report.getTotalExpense().doubleValue());
            row.createCell(5).setCellValue(report.getNetBalance().doubleValue());

            // ✅ Auto-size columns for better formatting
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ✅ Save file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            workbook.close();

            logger.info("✅ Excel report successfully generated: {}", fileName);
            return file;
        } catch (IOException e) {
            logger.error("❌ Error generating Excel report: {}", e.getMessage());
            return null;
        }
    }

}
