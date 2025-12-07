package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.SubscriptionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {

    /** Unique subscription ID */
    private Long subscriptionId;
    private Long userId;
    /** Subscription name (e.g., "Netflix") */
    private String name;

    /** Monthly charge amount */
    private BigDecimal amount;

    /** Next billing date */
    private LocalDateTime nextBillingDate;

    /** Payment method (Bank Account, Credit Card, etc.) */
    private String paymentMethodName;
    private Long paymentMethodId;

    /** Auto-renew status */
    private boolean autoRenew;

    /** Subscription status */
    private SubscriptionStatus status;

    /** Timestamp for when the subscription was created */
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
