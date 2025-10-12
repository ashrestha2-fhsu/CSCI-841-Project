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
public class InvestmentDTO {

    /** Unique investment ID */
    private Long investmentId;
    private BigDecimal performance;
    private Long userId;

    /** Type of investment (STOCKS, CRYPTO, MUTUAL_FUNDS, REAL_ESTATE) */
    private InvestmentType investmentType;

    /** Name of the investment asset (e.g., "Apple Stocks", "Bitcoin") */
    private String assetName;

    /** Initial amount invested */
    private BigDecimal amountInvested;

    /** Current market value of the investment */
    private BigDecimal currentValue;

    /** Date of purchase */
    private LocalDateTime purchaseDate;

    private boolean isDeleted;
    /** Last updated timestamp */
    private LocalDateTime lastUpdated;

    /** Timestamp for when the investment record was created */
    private LocalDateTime dateCreated;
}
