package CSCI_841_Project.backend.utility;

import lombok.*;

import java.math.BigDecimal;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReinvestmentRequest {
    private BigDecimal amountInvested;
    private BigDecimal additionalQuantity;
}
