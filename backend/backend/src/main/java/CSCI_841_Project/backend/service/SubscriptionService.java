package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.SubscriptionDTO;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getSubscriptionById(Long id);

    List<SubscriptionDTO> getSubscriptionsByUser(Long userId);

    SubscriptionDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO);

    void cancelSubscription(Long id);

//    void processSubscriptionBilling(); // ✅ Auto-charge subscriptions
}