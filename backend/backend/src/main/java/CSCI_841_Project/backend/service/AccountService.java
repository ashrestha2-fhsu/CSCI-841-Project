package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    AccountDTO createAccount(Long userId, AccountDTO accountDTO);

    AccountDTO getAccountById(Long accountId);

    List<AccountDTO> getAccountsByUser(Long userId);

    List<AccountDTO> getAllAccountsByUser(Long userId);

    AccountDTO updateAccount(Long accountId, AccountDTO accountDTO);

    void deleteAccount(Long accountId);

    void restoreAccount(Long accountId);

}
