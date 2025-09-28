package CSCI_841_Project.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentReportDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalInvested;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalReturns;
    private List<InvestmentDetailsDTO> investments;
}
