package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.SubscriptionDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Subscription;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.SubscriptionStatus;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.SubscriptionMapper;
import CSCI_841_Project.backend.repository.AccountRepository;
import CSCI_841_Project.backend.repository.SubscriptionRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.SubscriptionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImplementation implements SubscriptionService {

    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private SubscriptionMapper subscriptionMapper;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account paymentMethod = accountRepository.findById(dto.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        Subscription s = subscriptionMapper.toEntity(dto, user, paymentMethod);
        s = subscriptionRepository.save(s);
        return subscriptionMapper.toDTO(s);
    }

    /** Auto-billing runs only for ACTIVE & autoRenew=true */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processSubscriptionBilling() {
        List<Subscription> due = subscriptionRepository
                .findByNextBillingDateBeforeAndStatusAndAutoRenewIsTrue(
                        LocalDateTime.now(), SubscriptionStatus.ACTIVE);

        for (Subscription s : due) {
            Account acct = s.getPaymentMethod();
            BigDecimal amt = s.getAmount();
            if (acct.getBalance().compareTo(amt) >= 0) {
                acct.setBalance(acct.getBalance().subtract(amt));
                accountRepository.save(acct);

                s.setNextBillingDate(s.getNextBillingDate().plusMonths(1));
                s.setDateUpdated(LocalDateTime.now());
                subscriptionRepository.save(s);
            } else {
                s.setStatus(SubscriptionStatus.PAST_DUE);
                s.setDateUpdated(LocalDateTime.now());
                subscriptionRepository.save(s);
            }
        }
    }

    @Override
    public SubscriptionDTO getSubscriptionById(Long id) {
        Subscription s = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        return subscriptionMapper.toDTO(s);
    }

    @Override
    public List<SubscriptionDTO> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUser_UserId(userId)
                .stream().map(subscriptionMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionDTO updateSubscription(Long id, SubscriptionDTO dto) {
        Subscription s = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        if (dto.getName() != null) s.setName(dto.getName());
        if (dto.getAmount() != null) s.setAmount(dto.getAmount());
        if (dto.getNextBillingDate() != null) s.setNextBillingDate(dto.getNextBillingDate());
        s.setAutoRenew(dto.isAutoRenew());

        if (dto.getStatus() != null) s.setStatus(dto.getStatus());
        if (dto.getPaymentMethodId() != null) {
            Account pm = accountRepository.findById(dto.getPaymentMethodId())
                    .orElseThrow(() -> new NotFoundException("Payment method not found"));
            s.setPaymentMethod(pm);
        }

        s.setDateUpdated(LocalDateTime.now());
        subscriptionRepository.save(s);
        return subscriptionMapper.toDTO(s);
    }

    /** Pay Now is allowed ONLY when status = ACTIVE */
    @Override
    @Transactional
    public SubscriptionDTO payNow(Long subscriptionId, Long accountId, BigDecimal amount) {
        Subscription s = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        if (s.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Payment is allowed only when status is ACTIVE.");
        }

        Account src = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (amount == null) amount = s.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be > 0");

        if (src.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in account.");
        }

        src.setBalance(src.getBalance().subtract(amount));
        accountRepository.save(src);

        s.setNextBillingDate(
                (s.getNextBillingDate() != null ? s.getNextBillingDate() : LocalDateTime.now())
                        .plusMonths(1));
        if (s.getStatus() == SubscriptionStatus.PAST_DUE) {
            s.setStatus(SubscriptionStatus.ACTIVE);
        }
        s.setDateUpdated(LocalDateTime.now());
        subscriptionRepository.save(s);

        return subscriptionMapper.toDTO(s);
    }

    /** Pause */
    @Transactional
    public void pauseSubscription(Long id) {
        Subscription s = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        if (s.getStatus() == SubscriptionStatus.CANCELLED) return; // no-op
        s.setStatus(SubscriptionStatus.PAUSED);
        s.setAutoRenew(false);
        s.setDateUpdated(LocalDateTime.now());
        subscriptionRepository.save(s);
    }

    /** Resume */
    @Transactional
    public void resumeSubscription(Long id) {
        Subscription s = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        if (s.getStatus() == SubscriptionStatus.CANCELLED) return; // no-op
        s.setStatus(SubscriptionStatus.ACTIVE);
        s.setDateUpdated(LocalDateTime.now());
        subscriptionRepository.save(s);
    }

    /** Cancel (stops auto & marks cancelled) */
    @Override
    public void cancelSubscription(Long id) {
        Subscription s = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        s.setStatus(SubscriptionStatus.CANCELLED);
        s.setAutoRenew(false);
        s.setDateUpdated(LocalDateTime.now());
        subscriptionRepository.save(s);
    }



}
