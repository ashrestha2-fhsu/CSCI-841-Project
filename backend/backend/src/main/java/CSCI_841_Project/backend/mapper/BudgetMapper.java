package CSCI_841_Project.backend.mapper;


import CSCI_841_Project.backend.dto.BudgetDTO;
import CSCI_841_Project.backend.dto.BudgetDetailsDTO;
import CSCI_841_Project.backend.entity.Budget;
import CSCI_841_Project.backend.entity.Category;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.BudgetType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class BudgetMapper {

    @Autowired
    private TransactionRepository transactionRepository;

    public BudgetDTO toDTO(Budget budget) {
        BudgetDTO dto = new BudgetDTO();
        dto.setBudgetId(budget.getBudgetId());
        dto.setUserId(budget.getUser().getUserId());
        dto.setCategoryId(budget.getCategory().getCategoryId());
        dto.setAmountLimit(budget.getAmountLimit());
        dto.setStartDate(budget.getStartDate());
        dto.setDescription(budget.getDescription());
        dto.setEndDate(budget.getEndDate());
        dto.setBudgetType(budget.getBudgetType().name());
        dto.setRolloverAmount(budget.getRolloverAmount());
        dto.setDeleted(budget.isDeleted());
        dto.setDateCreated(budget.getDateCreated() != null ? budget.getDateCreated().atStartOfDay() : null); // ✅ Handle null case
        return dto;
    }

    public Budget toEntity(BudgetDTO budgetDTO, User user, Category category) {
        Budget budget = new Budget();
        budget.setUser(user);  // ✅ Assign User object
        budget.setCategory(category);  // ✅ Assign Category object
        budget.setAmountLimit(budgetDTO.getAmountLimit());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());

        // ✅ Prevent NullPointerException when converting BudgetType
        if (budgetDTO.getBudgetType() != null) {
            try {
                budget.setBudgetType(BudgetType.valueOf(budgetDTO.getBudgetType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Invalid Budget Type: " + budgetDTO.getBudgetType());
            }
        } else {
            throw new NotFoundException("Budget Type cannot be null!");
        }

        budget.setRolloverAmount(budgetDTO.getRolloverAmount());
        budget.setDescription(budgetDTO.getDescription()); // ✅ Fix incorrect mapping
        budget.setDeleted(budgetDTO.isDeleted());

        // ✅ Safely handle `dateCreated`
        budget.setDateCreated(budgetDTO.getDateCreated() != null ? budgetDTO.getDateCreated().toLocalDate() : LocalDate.now());

        return budget;
    }

    public BudgetDetailsDTO toDetailsDTO(Budget budget) {
        BudgetDetailsDTO dto = new BudgetDetailsDTO();

        dto.setBudgetId(budget.getBudgetId()); // ✅ Needed for delete
        dto.setDescription(budget.getDescription());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        dto.setAmountLimit(budget.getAmountLimit());
        dto.setBudgetType(budget.getBudgetType().name());
        dto.setRolloverAmount(budget.getRolloverAmount());
        dto.setCategory(budget.getCategory().getName());

        // ✅ Calculate SPENT
        BigDecimal spent = transactionRepository.sumExpensesInCategoryForPeriod(
                budget.getUser().getUserId(),
                budget.getCategory().getCategoryId(),
                budget.getStartDate().atStartOfDay(),
                budget.getEndDate().atTime(LocalTime.MAX)
        );

        if (spent == null) spent = BigDecimal.ZERO;
        dto.setSpent(spent);

        // ✅ Calculate % used
        BigDecimal percentUsed = BigDecimal.ZERO;
        if (budget.getAmountLimit().compareTo(BigDecimal.ZERO) > 0) {
            percentUsed = spent
                    .divide(budget.getAmountLimit(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        dto.setPercentageUsed(percentUsed.setScale(0, RoundingMode.HALF_UP).intValue());

        return dto;
    }
}
