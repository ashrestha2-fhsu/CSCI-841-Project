package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.SavingsGoalDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SavingsGoalService {

    /** ✅ Create a new savings goal */
    SavingsGoalDTO createSavingsGoal(SavingsGoalDTO savingsGoalDTO);

    /** ✅ Get savings goal by ID */
    Optional<SavingsGoalDTO> getSavingsGoalById(Long id);

    /** ✅ Get all savings goals for a user */
    List<SavingsGoalDTO> getSavingsGoalsByUser(Long userId);

    /** ✅ Update a savings goal */
    SavingsGoalDTO updateSavingsGoal(Long id, SavingsGoalDTO savingsGoalDTO);

    /** ✅ Delete a savings goal */
    void deleteSavingsGoal(Long id);

    /** ✅ Contribute money to a savings goal */
    SavingsGoalDTO contributeToSavings(Long savingsGoalId, BigDecimal amount);

}
