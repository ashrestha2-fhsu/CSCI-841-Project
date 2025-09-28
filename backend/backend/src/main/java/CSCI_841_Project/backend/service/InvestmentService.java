package CSCI_841_Project.backend.service;


import CSCI_841_Project.backend.dto.InvestmentDTO;

import java.util.List;

public interface InvestmentService {
    InvestmentDTO addInvestment(InvestmentDTO investmentDTO);

    InvestmentDTO updateInvestment(Long investmentId, InvestmentDTO investmentDTO);

//    void updateAllInvestments();

    InvestmentDTO getInvestmentById(Long id);

    List<InvestmentDTO> getInvestmentsByUser(Long userId);

    void deleteInvestment(Long id);

    void restoreInvestment(Long id);

    void simulateInvestmentGrowth();


}