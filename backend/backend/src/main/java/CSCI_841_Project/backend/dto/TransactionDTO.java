package CSCI_841_Project.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long transactionId;
    private Long userId;
    private Long accountId;
    private Long categoryId;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime date;
    private String description;
    private String paymentMethod;
    private String receiptUrl;
    private boolean isRecurring;
    private String recurringInterval;
    private LocalDateTime nextDueDate;
    private Long parentTransactionId;
    private String status;
    private Long toAccountId; // Added for transfers
    private boolean isDeleted;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
