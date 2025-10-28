package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.SubscriptionDTO;
import CSCI_841_Project.backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;


    /**
     * ✅ Create a New Subscription
     */
    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody SubscriptionDTO dto) {
        return ResponseEntity.ok(subscriptionService.createSubscription(dto));
    }

    /**
     * ✅ Get Subscription by ID
     */
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(subscriptionId));
    }

    /**
     * ✅ Get All Subscriptions for a User
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUser(userId));
    }


    /**
     * ✅ Update an Existing Subscription
     */
    @PutMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@PathVariable Long subscriptionId, @RequestBody SubscriptionDTO dto) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(subscriptionId, dto));
    }

    /**
     * ✅ Cancel a Subscription
     */
    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<String> cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.ok("Subscription cancelled successfully.");
    }
}
