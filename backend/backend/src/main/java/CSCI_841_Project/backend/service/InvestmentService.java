package CSCI_841_Project.backend.service;


import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.dto.InvestmentHistoryDTO;
import CSCI_841_Project.backend.dto.InvestmentReportDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvestmentService {

    InvestmentDTO updateInvestment(Long investmentId, InvestmentDTO dto, String username);

    InvestmentDTO addInvestment(InvestmentDTO dto);

    InvestmentDTO reinvest(Long investmentId, BigDecimal reinvestedAmount, BigDecimal reinvestedQuantity, String username);

    InvestmentDTO getInvestmentById(Long id);

    List<InvestmentDTO> getInvestmentsByUser(Long userId);

    void deleteInvestment(Long id);

    void restoreInvestment(Long id);

    void simulateInvestmentGrowth();

    InvestmentReportDTO getInvestmentReport(Long userId, LocalDate startDate, LocalDate endDate);

    List<InvestmentHistoryDTO> getInvestmentHistory(Long investmentId);

    Optional<InvestmentDTO> findBySymbol(Long userId, String symbol);


}