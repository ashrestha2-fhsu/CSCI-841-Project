package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.AssetSymbol;
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
public class InvestmentDTO {

    /** Unique investment ID */
    private Long investmentId;
    /** Type of investment (STOCKS, CRYPTO, MUTUAL_FUNDS, REAL_ESTATE) */
    private InvestmentType investmentType;
    /** Name of the investment asset (e.g., "Apple Stocks", "Bitcoin") */
    private String assetName;


    private BigDecimal performance;
    /** Initial amount invested */
    private BigDecimal totalAmountInvested;
    /** Current market value of the investment */
    private BigDecimal currentValue;

    private BigDecimal quantity;
    private String currency;
    private AssetSymbol assetSymbol;


    /** Date of purchase */
    private LocalDate purchaseDate;
    /** Last updated timestamp */
    private LocalDateTime lastUpdated;
    /** Timestamp for when the investment record was created */
    private LocalDateTime dateCreated;


    private Long userId;
    private boolean isDeleted;




}
