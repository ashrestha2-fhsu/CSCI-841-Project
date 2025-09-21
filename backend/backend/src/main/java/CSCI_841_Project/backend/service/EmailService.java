package CSCI_841_Project.backend.service;

import java.math.BigDecimal;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    /** ✅ Send savings contribution confirmation */
    void sendSavingsContributionEmail(Long userId, String goalName, BigDecimal contributionAmount);

    void send(String to, String subject, String body);
}
