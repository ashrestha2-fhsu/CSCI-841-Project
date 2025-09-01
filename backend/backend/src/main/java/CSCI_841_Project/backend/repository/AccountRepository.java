package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser_UserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountIdAndIsDeletedFalse(Long accountId); // ✅ Exclude deleted accounts
    List<Account> findByUser_UserIdAndIsDeletedFalse(Long userId); // ✅ Exclude deleted accounts

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.userId = :userId")
    BigDecimal getTotalAccountBalanceByUserId(@Param("userId") Long userId);

}
