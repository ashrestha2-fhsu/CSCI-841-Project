package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.InvestmentHistoryDTO;
import CSCI_841_Project.backend.entity.Investment;
import CSCI_841_Project.backend.entity.InvestmentHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InvestmentHistoryMapper {

    public InvestmentHistoryDTO toDTO(InvestmentHistory history) {
        if (history == null) return null;

        InvestmentHistoryDTO dto = new InvestmentHistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setInvestmentId(history.getInvestment().getInvestmentId());
        dto.setCurrentValue(history.getCurrentValue());
        dto.setPerformance(history.getPerformance());
        dto.setReturnsGenerated(history.getReturnsGenerated());
        dto.setRecordedAt(history.getRecordedAt());

        return dto;
    }


    public InvestmentHistory toEntity(InvestmentHistoryDTO dto, Investment investment) {
        if (dto == null) return null;

        InvestmentHistory history = new InvestmentHistory(); // ✅ Create instance first
        history.setInvestment(investment); // ✅ Set investment reference
        history.setCurrentValue(dto.getCurrentValue()); // ✅ Set current value
        history.setPerformance(dto.getPerformance()); // ✅ Set performance
        history.setRecordedAt(LocalDateTime.now()); // ✅ Set recorded timestamp
        history.setReturnsGenerated(dto.getReturnsGenerated());

        return history;
    }

}

