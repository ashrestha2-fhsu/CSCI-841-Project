package csci812_project.backend.mapper;

import csci812_project.backend.dto.SavingsGoalDTO;
import csci812_project.backend.entity.SavingsGoal;
import csci812_project.backend.entity.User;
import csci812_project.backend.enums.ContributionFrequency;
import csci812_project.backend.enums.PriorityLevel;
import csci812_project.backend.enums.SavingsGoalStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class SavingsGoalMapper {

    /**
     * ✅ Converts `SavingsGoal` entity → `SavingsGoalDTO`
     */
    public SavingsGoalDTO toDTO(SavingsGoal savingsGoal) {
        if (savingsGoal == null) return null;

        SavingsGoalDTO dto = new SavingsGoalDTO();
        dto.setGoalId(savingsGoal.getGoalId());
        dto.setUserId(savingsGoal.getUser().getUserId());
        dto.setGoalName(savingsGoal.getGoalName());
        dto.setTargetAmount(savingsGoal.getTargetAmount());
        dto.setCurrentAmount(savingsGoal.getCurrentAmount());
        dto.setDeadline(savingsGoal.getDeadline());
        dto.setStatus(savingsGoal.getStatus().name()); // Convert Enum to String
        dto.setAutoSave(savingsGoal.isAutoSave());
        dto.setPriorityLevel(savingsGoal.getPriorityLevel().name()); // Convert Enum to String
        dto.setContributionFrequency(savingsGoal.getContributionFrequency().name()); // Convert Enum to String
        dto.setDeleted(savingsGoal.isDeleted());


        dto.setDateCreated(savingsGoal.getDateCreated().atStartOfDay());
        dto.setDateUpdated(savingsGoal.getDateUpdated().atStartOfDay());

        return dto;
    }

    /**
     * ✅ Converts `SavingsGoalDTO` → `SavingsGoal` entity
     */
    public SavingsGoal toEntity(SavingsGoalDTO dto, User user) {
        if (dto == null) return null;

        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setUser(user);
        savingsGoal.setGoalName(dto.getGoalName());
        savingsGoal.setTargetAmount(dto.getTargetAmount());
        savingsGoal.setCurrentAmount(dto.getCurrentAmount() != null ? dto.getCurrentAmount() : BigDecimal.ZERO);
        // ✅ Ensure `deadline` is not null
        savingsGoal.setDeadline(dto.getDeadline() != null ? dto.getDeadline() : LocalDate.now().plusMonths(6));
        savingsGoal.setStatus(SavingsGoalStatus.valueOf(dto.getStatus())); // Convert String to Enum
        savingsGoal.setAutoSave(dto.isAutoSave());
        savingsGoal.setPriorityLevel(PriorityLevel.valueOf(dto.getPriorityLevel())); // Convert String to Enum
        savingsGoal.setContributionFrequency(ContributionFrequency.valueOf(dto.getContributionFrequency())); // Convert String to Enum
        savingsGoal.setDeleted(dto.isDeleted());

        // ✅ Ensure `dateCreated` is not null
        savingsGoal.setDateCreated(dto.getDateCreated() != null ? dto.getDateCreated().toLocalDate() : LocalDateTime.now().toLocalDate());

        // ✅ Ensure `dateUpdated` is not null
        savingsGoal.setDateUpdated(dto.getDateUpdated() != null ? dto.getDateUpdated().toLocalDate() : LocalDateTime.now().toLocalDate());

        return savingsGoal;
    }

}

