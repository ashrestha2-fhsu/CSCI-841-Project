package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.InvestmentHistoryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InvestmentHistoryService {

    List<InvestmentHistoryDTO> getInvestmentHistory(Long investmentId);

    void recordInvestmentHistory(Long investmentId, BigDecimal newCurrentValue);


    List<InvestmentHistoryDTO> getInvestmentHistoryByDateRange(Long investmentId,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);
//    void recordInvestmentsHistory(Long investmentId);
}
