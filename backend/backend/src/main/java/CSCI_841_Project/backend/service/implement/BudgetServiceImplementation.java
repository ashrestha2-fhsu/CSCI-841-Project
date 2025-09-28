package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.BudgetDTO;
import CSCI_841_Project.backend.dto.BudgetDetailsDTO;
import CSCI_841_Project.backend.dto.BudgetReportDTO;
import CSCI_841_Project.backend.entity.Budget;
import CSCI_841_Project.backend.entity.Category;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.BudgetType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.BudgetMapper;
import CSCI_841_Project.backend.repository.BudgetRepository;
import CSCI_841_Project.backend.repository.CategoryRepository;
import CSCI_841_Project.backend.repository.TransactionRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImplementation implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetMapper budgetMapper;
    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        User user = userRepository.findById(budgetDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(budgetDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Budget budget = budgetMapper.toEntity(budgetDTO, user, category);
        budget.setUser(user);
        budget.setCategory(category);

        return budgetMapper.toDTO(budgetRepository.save(budget));
    }

    @Override
    public BudgetDTO getBudgetById(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .filter(a -> !a.isDeleted())  // excludes soft deleted budgets
                .orElseThrow(() -> new NotFoundException("Budget not found"));
        return budgetMapper.toDTO(budget);

    }

    @Override
    public List<BudgetDTO> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUser_UserId(userId)
                .stream()
                .filter(budget -> !budget.isDeleted())   // ✅ Exclude soft-deleted accounts
                .map(budgetMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetDTO updateBudget(Long id, BudgetDTO budgetDTO) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget not found"));

        budget.setAmountLimit(budgetDTO.getAmountLimit());
        budget.setRolloverAmount(budgetDTO.getRolloverAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());
        budget.setDescription(budget.getDescription());
        budget.setBudgetType(BudgetType.valueOf(budgetDTO.getBudgetType()));

        return budgetMapper.toDTO(budgetRepository.save(budget));
    }

    @Override
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget not found"));

        budget.setDeleted(true);
        budgetRepository.save(budget);
    }

    /**
     * ✅ Check if the user is close to exceeding their budget (80% usage threshold).
     */
    @Override
    public boolean checkBudgetUsage(Long userId, Long categoryId, BigDecimal transactionAmount) {
        List<Budget> budgets = budgetRepository.findByUser_UserIdAndCategory_CategoryId(userId, categoryId);

        for (Budget budget : budgets) {
            BigDecimal spentAmount = budgetRepository.getTotalSpentInBudget(userId, categoryId);
            BigDecimal newTotalSpent = spentAmount.add(transactionAmount);
            BigDecimal alertThreshold = budget.getAmountLimit().multiply(BigDecimal.valueOf(0.8));

            if (newTotalSpent.compareTo(alertThreshold) >= 0) {
                return true; // Trigger an alert
            }
        }
        return false;
    }

    @Override
    public BudgetReportDTO getBudgetReport(Long userId) {
        List<Budget> budgets = budgetRepository.findByUser_UserId(userId)
                .stream()
                .filter(b -> !b.isDeleted())
                .collect(Collectors.toList());

        List<BudgetDetailsDTO> details = budgets.stream()
                .map(budgetMapper::toDetailsDTO)
                .collect(Collectors.toList());

        BigDecimal totalBudgetLimit = budgets.stream()
                .map(Budget::getAmountLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRolloverAmount = budgets.stream()
                .map(b -> b.getRolloverAmount() != null ? b.getRolloverAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate minStartDate = budgets.stream()
                .map(Budget::getStartDate)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate maxEndDate = budgets.stream()
                .map(Budget::getEndDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        BudgetReportDTO report = new BudgetReportDTO();
        report.setUserId(userId);
        report.setBudgets(details);
        report.setTotalBudgetLimit(totalBudgetLimit);
        report.setTotalRolloverAmount(totalRolloverAmount);
        report.setStartDate(minStartDate);
        report.setEndDate(maxEndDate);

        return report;
    }

    @Override
    public Optional<Budget> findActiveBudget(Long userId, Long categoryId, LocalDate transactionDate) {
        return budgetRepository.findFirstByUser_UserIdAndCategory_CategoryIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsDeletedFalse(
                userId, categoryId, transactionDate, transactionDate
        );
    }

    // This method determines if a transaction would exceed the limit.
    @Override
    public boolean isTransactionWithinBudget(Long userId, Long categoryId, BigDecimal transactionAmount, LocalDateTime transactionDate) {
        Optional<Budget> optionalBudget = findActiveBudget(userId, categoryId, transactionDate.toLocalDate());
        if (optionalBudget.isEmpty()) return true; // No budget => allow transaction

        Budget budget = optionalBudget.get();

        BigDecimal spent = transactionRepository.sumExpensesInCategoryForPeriod(
                userId,
                categoryId,
                budget.getStartDate().atStartOfDay(),
                budget.getEndDate().atTime(LocalTime.MAX)
        );
        BigDecimal projected = spent.add(transactionAmount);

        // ✅ Use BudgetType to determine allowance
        if (budget.getBudgetType() == BudgetType.STRICT) {
            return projected.compareTo(budget.getAmountLimit()) <= 0;
        }
        return true; // FLEXIBLE: Allow even if over budget
    }

}