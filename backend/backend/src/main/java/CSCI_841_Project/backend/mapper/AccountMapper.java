package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.AccountDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.enums.AccountType;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDTO toDTO(Account account){
        if (account == null){
            return null;
        }

        AccountDTO dto = new AccountDTO();
        dto.setAccountId(account.getAccountId());
        dto.setUserId(account.getUser().getUserId());
        dto.setName(account.getName());
        dto.setType(account.getType().name());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setInterestRate(account.getInterestRate());
        dto.setDefault(account.isDefault());
        dto.setDeleted(account.isDeleted());
        dto.setDateCreated(account.getDateCreated());
        dto.setDateUpdated(account.getDateUpdated());

        return dto;
    }

    public Account toEntity(AccountDTO accountDTO){
        if (accountDTO == null){
            return  null;
        }

        Account account = new Account();
        account.setAccountId(accountDTO.getAccountId());
        account.setName(accountDTO.getName());
        account.setType(AccountType.valueOf(accountDTO.getType()));
        account.setBalance(accountDTO.getBalance());
        account.setCurrency(accountDTO.getCurrency());
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setInterestRate(accountDTO.getInterestRate());
        account.setDeleted(accountDTO.isDeleted());
        account.setDefault(accountDTO.isDefault());
        account.setDateCreated(accountDTO.getDateCreated());
        account.setDateUpdated(accountDTO.getDateUpdated());

        return account;
    }
}
