package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.PaymentMethod;
import CSCI_841_Project.backend.enums.TransactionStatus;
import CSCI_841_Project.backend.enums.TransactionType;
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
public class TransactionDetailsDTO {
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime date;
    private PaymentMethod paymentMethod;
    private LocalDate nextDueDate;
    private Long toAccountId;
    private LocalDateTime dateCreated;
    private TransactionStatus status;
    private String accountName; // From Account entity
    private String category; // From Category entity
}
