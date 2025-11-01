package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequestDTO {

    private InvestmentType investmentType;
    private String assetName;
    private BigDecimal totalAmountInvested;
    private BigDecimal currentValue;
    private LocalDateTime purchaseDate;

    private BigDecimal quantity;
    private String currency;
    private String assetSymbol;

    private Long userId; // optional if derived from auth token
}
