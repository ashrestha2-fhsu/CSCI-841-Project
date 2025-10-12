package CSCI_841_Project.backend.mapper;


import CSCI_841_Project.backend.dto.TransactionDTO;
import CSCI_841_Project.backend.dto.TransactionDetailsDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Transaction;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.PaymentMethod;
import CSCI_841_Project.backend.enums.RecurringInterval;
import CSCI_841_Project.backend.enums.TransactionStatus;
import CSCI_841_Project.backend.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) return null;

        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUserId(transaction.getUser().getUserId());
        dto.setAccountId(transaction.getAccount().getAccountId());
        dto.setToAccountId(transaction.getToAccount() != null ? transaction.getToAccount().getAccountId() : null); // Handle transfers
        dto.setCategoryId(transaction.getCategory() != null ? transaction.getCategory().getCategoryId() : null); // Ensure categoryId is set
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setPaymentMethod(transaction.getPaymentMethod().name());
        dto.setDescription(transaction.getDescription());
        dto.setStatus(transaction.getStatus().name());
        dto.setRecurring(transaction.isRecurring());
        dto.setDateCreated(transaction.getDateCreated());
        return dto;
    }

    public Transaction toEntity(TransactionDTO transactionDTO, User user, Account account, Account toAccount) {
        if (transactionDTO == null) return null;

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setToAccount(toAccount);


        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionType(TransactionType.valueOf(transactionDTO.getTransactionType()));
        transaction.setDate(transactionDTO.getDate() != null ? transactionDTO.getDate() : LocalDateTime.now());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setPaymentMethod(transactionDTO.getPaymentMethod() != null ?
                PaymentMethod.valueOf(transactionDTO.getPaymentMethod()) : null);
        transaction.setReceiptUrl(transactionDTO.getReceiptUrl());
        transaction.setRecurring(transactionDTO.isRecurring());
        if (transactionDTO.getRecurringInterval() != null) {
            transaction.setRecurringInterval(RecurringInterval.valueOf(transactionDTO.getRecurringInterval().toUpperCase()));
        }

        transaction.setNextDueDate(transactionDTO.getNextDueDate());
        transaction.setParentTransaction(null);
        transaction.setStatus(transactionDTO.getStatus() != null ?
                TransactionStatus.valueOf(transactionDTO.getStatus()) : TransactionStatus.COMPLETED);
        transaction.setDeleted(false);
        transaction.setDateCreated(LocalDateTime.now());
        transaction.setDateUpdated(LocalDateTime.now());

        return transaction;
    }

    // TransactionDetailsDto
    public TransactionDetailsDTO toDetailsDTO(Transaction tx) {
        TransactionDetailsDTO dto = new TransactionDetailsDTO();
        dto.setAmount(tx.getAmount());
        dto.setTransactionType(tx.getTransactionType());
        dto.setDescription(tx.getDescription());
        dto.setDate(tx.getDate());
        dto.setPaymentMethod(tx.getPaymentMethod());
        dto.setNextDueDate(tx.getNextDueDate() != null ? tx.getNextDueDate().toLocalDate() : null);
        dto.setToAccountId(tx.getToAccount() != null ? tx.getToAccount().getAccountId() : null);
        dto.setDateCreated(tx.getDateCreated());
        dto.setStatus(tx.getStatus());

        // 🔗 Add readable names
        dto.setAccountName(tx.getAccount() != null ? tx.getAccount().getName() : null);
        dto.setCategory(tx.getCategory() != null ? tx.getCategory().getName() : null);

        return dto;
    }
}
