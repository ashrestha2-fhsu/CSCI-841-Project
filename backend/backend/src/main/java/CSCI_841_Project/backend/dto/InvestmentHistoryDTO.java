package CSCI_841_Project.backend.dto;

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
public class InvestmentHistoryDTO {

    private Long historyId;
    private Long investmentId;
    private BigDecimal currentValue;
    private BigDecimal performance;
    private LocalDateTime recordedAt;
    private BigDecimal returnsGenerated;
}
