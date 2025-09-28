package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDetailsDTO {

    private InvestmentType investmentType;
    private String assetName;
    private BigDecimal amountInvested;
    private BigDecimal currentValue;
    private LocalDate purchaseDate;
    private LocalDateTime lastUpdated;
    private BigDecimal performance;
}
