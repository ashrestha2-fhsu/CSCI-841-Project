package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.SubscriptionDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Subscription;
import CSCI_841_Project.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    /** ✅ Convert `Subscription` Entity → `SubscriptionDTO` */
    public SubscriptionDTO toDTO(Subscription subscription) {
        if (subscription == null) return null;

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setUserId(subscription.getUser().getUserId());
        dto.setName(subscription.getName());
        dto.setAmount(subscription.getAmount());
        dto.setNextBillingDate(subscription.getNextBillingDate());
        dto.setPaymentMethodId(subscription.getPaymentMethod().getAccountId());
        dto.setAutoRenew(subscription.isAutoRenew());
        dto.setStatus(subscription.getStatus());
        dto.setDateCreated(subscription.getDateCreated());
        dto.setDateUpdated(subscription.getDateUpdated());
        return dto;
    }

    /** ✅ Convert `SubscriptionDTO` → `Subscription` Entity */
    public Subscription toEntity(SubscriptionDTO dto, User user, Account paymentMethod) {
        if (dto == null) return null;

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setName(dto.getName());
        subscription.setAmount(dto.getAmount());
        subscription.setNextBillingDate(dto.getNextBillingDate());
        subscription.setPaymentMethod(paymentMethod);
        subscription.setAutoRenew(dto.isAutoRenew());
        subscription.setStatus(dto.getStatus());
        return subscription;
    }
}