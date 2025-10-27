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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImplementation implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SubscriptionMapper subscriptionMapper;


    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account paymentMethod = accountRepository.findById(dto.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        Subscription subscription = subscriptionMapper.toEntity(dto, user, paymentMethod);
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(subscription);
    }

    /** ✅ Automatically charge subscriptions */
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void processSubscriptionBilling() {
        List<Subscription> dueSubscriptions = subscriptionRepository.findByNextBillingDateBeforeAndStatus(
                LocalDateTime.now(), SubscriptionStatus.ACTIVE
        );

        for (Subscription subscription : dueSubscriptions) {
            Account paymentMethod = subscription.getPaymentMethod();
            if (paymentMethod.getBalance().compareTo(subscription.getAmount()) >= 0) {
                paymentMethod.setBalance(paymentMethod.getBalance().subtract(subscription.getAmount()));
                subscription.setNextBillingDate(subscription.getNextBillingDate().plusMonths(1));
                accountRepository.save(paymentMethod);
                subscriptionRepository.save(subscription);
            }
        }
    }

    @Override
    public SubscriptionDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        return subscriptionMapper.toDTO(subscription);
    }


    @Override
    public List<SubscriptionDTO> getSubscriptionsByUser(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUser_UserId(userId);

        return subscriptions.stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public SubscriptionDTO updateSubscription(Long id, SubscriptionDTO dto) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        subscription.setName(dto.getName());
        subscription.setAmount(dto.getAmount());
        subscription.setNextBillingDate(dto.getNextBillingDate());
        subscription.setAutoRenew(dto.isAutoRenew());
        subscription.setStatus(SubscriptionStatus.valueOf(dto.getStatus().name()));
        subscription.setDateUpdated(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(subscription);
    }


    @Override
    public void cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        subscription.setDateUpdated(LocalDateTime.now());

        subscriptionRepository.save(subscription);
    }

}
