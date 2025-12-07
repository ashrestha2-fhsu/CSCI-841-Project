package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.dto.InvestmentDetailsDTO;
import CSCI_841_Project.backend.dto.InvestmentHistoryDTO;
import CSCI_841_Project.backend.entity.Investment;
import CSCI_841_Project.backend.entity.InvestmentHistory;
import CSCI_841_Project.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InvestmentMapper {

    public InvestmentDTO toDTO(Investment investment) {
        if (investment == null) return null;

        InvestmentDTO dto = new InvestmentDTO();
        dto.setInvestmentId(investment.getInvestmentId());
        dto.setUserId(investment.getUser().getUserId());
        dto.setInvestmentType(investment.getInvestmentType());
        dto.setAssetName(investment.getAssetName());
        dto.setTotalAmountInvested(investment.getTotalAmountInvested());
        dto.setCurrentValue(investment.getCurrentValue());
        dto.setPurchaseDate(investment.getPurchaseDate());
        dto.setPerformance(investment.getPerformance());
        dto.setLastUpdated(investment.getLastUpdated());
        dto.setDeleted(investment.isDeleted());
        dto.setDateCreated(investment.getDateCreated());

        // ✅ New fields
        dto.setQuantity(investment.getQuantity());
        dto.setAssetSymbol(investment.getAssetSymbol());
        dto.setCurrency(investment.getCurrency());

        return dto;
    }

    public Investment toEntity(InvestmentDTO dto, User user) {
        if (dto == null) return null;

        Investment investment = new Investment();
        investment.setUser(user);
        investment.setInvestmentType(dto.getInvestmentType());
        investment.setAssetName(dto.getAssetName());
        investment.setTotalAmountInvested(dto.getTotalAmountInvested());
        investment.setCurrentValue(dto.getCurrentValue());
        investment.setPurchaseDate(dto.getPurchaseDate());

        // ✅ New fields
        investment.setQuantity(dto.getQuantity());
        investment.setAssetSymbol(dto.getAssetSymbol());
        investment.setCurrency(dto.getCurrency());

        return investment;
    }

    public InvestmentDetailsDTO toDetailsDTO(Investment investment) {
        if (investment == null) return null;

        InvestmentDetailsDTO dto = new InvestmentDetailsDTO();
        dto.setInvestmentType(investment.getInvestmentType());
        dto.setAssetName(investment.getAssetName());
        dto.setTotalAmountInvested(investment.getTotalAmountInvested());
        dto.setCurrentValue(investment.getCurrentValue());
        dto.setPurchaseDate(investment.getPurchaseDate());
        dto.setLastUpdated(investment.getLastUpdated());
        dto.setPerformance(investment.getPerformance());

        return dto;
    }

    public InvestmentHistoryDTO toHistoryDTO(InvestmentHistory history) {
        if (history == null) return null;

        InvestmentHistoryDTO dto = new InvestmentHistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setInvestmentId(history.getInvestment().getInvestmentId());
        dto.setCurrentValue(history.getCurrentValue());
        dto.setPerformance(history.getPerformance());
        dto.setRecordedAt(history.getRecordedAt());
        dto.setReturnsGenerated(history.getReturnsGenerated());

        return dto;
    }


}
