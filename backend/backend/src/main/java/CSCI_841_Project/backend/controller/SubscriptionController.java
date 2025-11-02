package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.SubscriptionDTO;
import CSCI_841_Project.backend.service.SubscriptionService;
import CSCI_841_Project.backend.service.implement.SubscriptionServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired private SubscriptionServiceImplementation service;

    @PostMapping
    public ResponseEntity<SubscriptionDTO> create(@RequestBody SubscriptionDTO dto) {
        return ResponseEntity.ok(service.createSubscription(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSubscriptionById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionDTO>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getSubscriptionsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> update(@PathVariable Long id, @RequestBody SubscriptionDTO dto) {
        return ResponseEntity.ok(service.updateSubscription(id, dto));
    }

    @PutMapping("/{id}/pause")
    public ResponseEntity<Void> pause(@PathVariable Long id) {
        service.pauseSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/resume")
    public ResponseEntity<Void> resume(@PathVariable Long id) {
        service.resumeSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancelSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payNow")
    public ResponseEntity<SubscriptionDTO> payNow(
            @PathVariable Long id,
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(service.payNow(id, accountId, amount));
    }


}
