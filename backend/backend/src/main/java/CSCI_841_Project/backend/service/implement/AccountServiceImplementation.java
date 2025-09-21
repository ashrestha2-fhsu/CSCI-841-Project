package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.AccountDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.AccountType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.AccountMapper;
import CSCI_841_Project.backend.repository.AccountRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountMapper accountMapper;

    // CREATE NEW ACCOUNT METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public AccountDTO createAccount(Long userId, AccountDTO accountDTO){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User Not Found"));

        Account account = accountMapper.toEntity(accountDTO);
        account.setUser(user);
        account = accountRepository.save(account);
        return accountMapper.toDTO(account);
    }

    // GET ACCOUNT BY ID METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public AccountDTO getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .filter(a-> !a.isDeleted())
                .orElseThrow(()-> new NotFoundException("Account Not Found or has  been deleted"));

        return accountMapper.toDTO(account);
    }

    // GET ACCOUNT BY USER METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public List<AccountDTO> getAccountsByUser(Long userId) {
        // Check if the user exists before fetching accounts
        if (userRepository.existsById(userId)){
            throw new NotFoundException("User with ID "+ userId + " not found"); // ✅ Throws a 404 error
        }

        return accountRepository.findByUser_UserId(userId)
                .stream()
                .filter(account -> !account.isDeleted()) // ✅ Exclude soft-deleted accounts
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());

    }

    // GET ALL ACCOUNTS BOTH SOFT-DELETE AND NON-DELETE METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public List<AccountDTO> getAllAccountsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        // ✅ Do not filter out deleted accounts
        return accountRepository.findByUser_UserId(userId)
                .stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    // UPDATE USER  ACCOUNT  METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public AccountDTO updateAccount(Long accountId, AccountDTO accountDTO) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));
        
        existingAccount.setName(accountDTO.getName());
        existingAccount.setType(AccountType.valueOf(accountDTO.getType().toUpperCase()));
        existingAccount.setBalance(accountDTO.getBalance());
        existingAccount.setInterestRate(accountDTO.getInterestRate());
        existingAccount.setCurrency(accountDTO.getCurrency());
        
        accountRepository.save(existingAccount);
        
        return accountMapper.toDTO(existingAccount);
    }

    // DELETE USER  ACCOUNT  METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));
        
        account.setDeleted(true);
        accountRepository.save(account);

    }

    // RESTORE DELETED USER  ACCOUNT  METHOD  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void restoreAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));

        account.setDeleted(false);
        accountRepository.save(account);
    }
}
