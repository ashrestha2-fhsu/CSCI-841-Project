package CSCI_841_Project.backend.service.implement;


import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {

//    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

//    @Value("${sendgrid.sender.email}")
    private String senderEmail;


    @Autowired
    private UserRepository userRepository;


    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("📩 Attempting to send email to: " + to); // ✅ Log recipient email
        System.out.println("📤 Sending from: " + senderEmail); // ✅ Log sender email

        Email from = new Email(senderEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("✅ Email sent successfully to: " + to);
            } else {
                System.err.println("🚨 Email failed: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (IOException e) {
            System.err.println("🚨 Error sending email: " + e.getMessage());
        }
    }

    @Override
    public void sendSavingsContributionEmail(Long userId, String goalName, BigDecimal contributionAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("🚨 User not found"));

        String subject = "✅ Auto-Savings Contribution Completed";
        String body = "<p>Dear " + user.getFirstName() + ",</p>"
                + "<p>A contribution of <strong>$" + contributionAmount + "</strong> has been successfully added to your savings goal <strong>" + goalName + "</strong>. 🎯</p>"
                + "<p>Keep saving and reach your goal!</p>"
                + "<p>Best regards,<br><strong>Your Personal Finance Tracker</strong></p>";

        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void send(String to, String subject, String body) {
        sendEmail(to, subject, body);
    }
}

