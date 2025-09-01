package CSCI_841_Project.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class AccountDTO {
    private Long accountId;
    private Long userId;
    private String name;
//    private String institutionName;
    private String type;
    private BigDecimal balance;
    private  String currency;
    private  String accountNumber;
    private BigDecimal interestRate;
    private boolean isDefault;
    private boolean isDeleted;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    // CONSTRUCTOR

    // GETTER AND SETTER

}
