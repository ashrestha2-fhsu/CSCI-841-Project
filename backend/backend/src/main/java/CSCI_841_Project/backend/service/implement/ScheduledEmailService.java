package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.entity.Budget;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.repository.BudgetRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledEmailService {

    @Autowired
    private EmailService emailService;
//    @Autowired
//    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BudgetRepository budgetRepository;
//    @Autowired
//    private SavingsGoalRepository savingsGoalRepository;




    /** ✅ Send Loan Reminders Every Day at 9 AM */
//    @Scheduled(cron = "0 0 9 * * ?")
//    public void sendLoanReminders() {
//        List<Loan> loans = loanRepository.findLoansDueSoon();
//        for (Loan loan : loans) {
//            User user = loan.getUser();
//            emailService.sendEmail(
//                    user.getEmail(),
//                    "Loan Payment Reminder - " + loan.getLenderName(),
//                    "<p>Hello " + user.getFirstName() + ",</p>"
//                            + "<p>Your loan payment for <strong>" + loan.getLenderName() + "</strong> is due on <strong>"
//                            + loan.getDueDate() + "</strong>.</p>"
//                            + "<p>Amount due: <strong>$" + loan.getMonthlyPayment() + "</strong>.</p>"
//                            + "<p>Please ensure timely payment to avoid late fees.</p>"
//                            + "<p>Best regards,<br>Your Finance Tracker Team</p>"
//            );
//        }
//        System.out.println("✅ Loan reminders sent successfully!");
//    }

    /** ✅ Send Budget Alerts Every Monday at 10 AM */
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendBudgetAlerts() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Optional<Budget> latestBudgetOpt = budgetRepository.findLatestBudgetByUser(user.getUserId());

            if (latestBudgetOpt.isPresent()) {
                Budget latestBudget = latestBudgetOpt.get();
                BigDecimal budgetLimit = latestBudget.getAmountLimit();
                BigDecimal currentSpending = latestBudget.getRolloverAmount(); // Assuming rolloverAmount stores current spending

                if (currentSpending.compareTo(budgetLimit) >= 0) {
                    emailService.sendEmail(
                            user.getEmail(),
                            "⚠️ Budget Alert: You're close to exceeding your limit!",
                            "<p>Dear " + user.getFirstName() + ",</p>"
                                    + "<p>You have spent nearly all of your budget.</p>"
                                    + "<p>Your limit is: <strong>$" + budgetLimit + "</strong>.</p>"
                                    + "<p>Please manage your expenses wisely!</p>"
                                    + "<p>Best,<br>Finance Tracker Team</p>"
                    );
                }
            }
        }
        System.out.println("✅ Budget alerts sent!");
    }

    /** ✅ Send Savings Goal Reminders on the 1st of Every Month at 8 AM */
//    @Scheduled(cron = "0 0 8 1 * ?")
//    public void sendSavingsGoalReminders() {
//        List<User> users = userRepository.findAll();
//        for (User user : users) {
//            Optional<SavingsGoal> latestGoalOpt = savingsGoalRepository.findLatestSavingsGoalByUser(user.getUserId());
//
//            if (latestGoalOpt.isPresent()) {
//                SavingsGoal latestGoal = latestGoalOpt.get();
//                BigDecimal targetAmount = latestGoal.getTargetAmount();
//                BigDecimal currentAmount = latestGoal.getCurrentAmount();
//                BigDecimal progress = currentAmount.divide(targetAmount, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
//
//                if (progress.compareTo(BigDecimal.valueOf(50)) < 0) {
//                    emailService.sendEmail(
//                            user.getEmail(),
//                            "🚀 Savings Goal Update: Keep Going!",
//                            "<p>Dear " + user.getFirstName() + ",</p>"
//                                    + "<p>Your savings goal is now <strong>" + progress + "% complete</strong>. 🎉</p>"
//                                    + "<p>Keep up the great work!</p>"
//                                    + "<p>Best,<br>Finance Tracker Team</p>"
//                    );
//                }
//            }
//        }
//        System.out.println("✅ Savings reminders sent!");
//    }
}

