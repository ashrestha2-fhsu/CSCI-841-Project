package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.entity.Investment;
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
        dto.setAmountInvested(investment.getAmountInvested());
        dto.setPerformance(investment.getPerformance());
        dto.setCurrentValue(investment.getCurrentValue());
        dto.setPurchaseDate(investment.getPurchaseDate());
        dto.setLastUpdated(investment.getLastUpdated());
        dto.setDeleted(investment.isDeleted());
        dto.setDateCreated(investment.getDateCreated());

        return dto;
    }

    public Investment toEntity(InvestmentDTO dto, User user) {
        if (dto == null) return null;

        Investment investment = new Investment();
        investment.setUser(user);
        investment.setInvestmentType(dto.getInvestmentType());
        investment.setAssetName(dto.getAssetName());
        investment.setAmountInvested(dto.getAmountInvested());
        investment.setCurrentValue(dto.getCurrentValue());
        investment.setPurchaseDate(dto.getPurchaseDate());
        return investment;
    }

}
