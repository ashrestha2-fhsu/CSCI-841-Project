package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.SubscriptionDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.Subscription;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.SubscriptionStatus;
import io.micrometer.common.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionMapper {

    /** Entity -> DTO (UI-friendly, null-safe, includes paymentMethodName) */
    public SubscriptionDTO toDTO(Subscription s) {
        if (s == null) return null;

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setSubscriptionId(s.getSubscriptionId());
        dto.setUserId(s.getUser().getUserId());
        dto.setName(s.getName());
        dto.setAmount(s.getAmount());
        dto.setNextBillingDate(s.getNextBillingDate());

        if (s.getPaymentMethod() != null) {
            dto.setPaymentMethodId(s.getPaymentMethod().getAccountId());
            // helpful for tables/dropdowns in the UI
            dto.setPaymentMethodName(s.getPaymentMethod().getName());
        }

        dto.setAutoRenew(s.isAutoRenew());
        dto.setStatus(s.getStatus());
        dto.setDateCreated(s.getDateCreated());
        dto.setDateUpdated(s.getDateUpdated());
        return dto;
    }

    /** DTO -> Entity (create). Applies safe defaults for missing fields. */
    public Subscription toEntity(SubscriptionDTO dto, User user, Account paymentMethod) {
        if (dto == null) return null;

        Subscription s = new Subscription();
        s.setUser(user);
        s.setPaymentMethod(paymentMethod);
        s.setName(dto.getName());
        s.setAmount(dto.getAmount());

        // default nextBillingDate if client omitted it
        s.setNextBillingDate(
                dto.getNextBillingDate() != null ? dto.getNextBillingDate()
                        : LocalDateTime.now().plusMonths(1)
        );

        s.setAutoRenew(dto.isAutoRenew());
        s.setStatus(dto.getStatus() != null ? dto.getStatus()
                : SubscriptionStatus.ACTIVE);

        // ensure timestamps are set
        s.setDateCreated(LocalDateTime.now());
        s.setDateUpdated(LocalDateTime.now());
        return s;
    }

    /** In-place updater used by the service layer (optional but clean). */
    public void applyUpdate(Subscription s, SubscriptionDTO dto, @Nullable Account newPaymentMethod) {
        if (dto == null || s == null) return;

        if (dto.getName() != null)          s.setName(dto.getName());
        if (dto.getAmount() != null)        s.setAmount(dto.getAmount());
        if (dto.getNextBillingDate() != null) s.setNextBillingDate(dto.getNextBillingDate());
        if (dto.getStatus() != null)        s.setStatus(dto.getStatus());
        // booleans usually mean “explicit choice from UI”; always copy
        s.setAutoRenew(dto.isAutoRenew());

        if (newPaymentMethod != null)       s.setPaymentMethod(newPaymentMethod);

        s.setDateUpdated(LocalDateTime.now());
    }

}