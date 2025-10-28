package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.SavingsGoalDTO;
import CSCI_841_Project.backend.entity.Account;
import CSCI_841_Project.backend.entity.SavingsGoal;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.ContributionFrequency;
import CSCI_841_Project.backend.enums.PriorityLevel;
import CSCI_841_Project.backend.enums.SavingsGoalStatus;
import CSCI_841_Project.backend.mapper.SavingsGoalMapper;
import CSCI_841_Project.backend.repository.AccountRepository;
import CSCI_841_Project.backend.repository.SavingsGoalRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.EmailService;
import CSCI_841_Project.backend.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsGoalServiceImplementation implements SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SavingsGoalMapper savingsGoalMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private  ScheduledEmailService scheduledEmailService;


    /** ✅ Create a new savings goal */
    @Override
    @Transactional
    public SavingsGoalDTO createSavingsGoal(SavingsGoalDTO savingsGoalDTO) {
        User user = userRepository.findById(savingsGoalDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Set default values for `dateCreated` and `dateUpdated` if missing
        if (savingsGoalDTO.getDateCreated() == null) {
            savingsGoalDTO.setDateCreated(LocalDateTime.now());
        }
        if (savingsGoalDTO.getDateUpdated() == null) {
            savingsGoalDTO.setDateUpdated(LocalDateTime.now());
        }

        SavingsGoal savingsGoal = savingsGoalMapper.toEntity(savingsGoalDTO, user);

        return savingsGoalMapper.toDTO(savingsGoalRepository.save(savingsGoal));
    }



    /** ✅ Get savings goal by ID */
    @Override
    public Optional<SavingsGoalDTO> getSavingsGoalById(Long id) {
        return savingsGoalRepository.findById(id)
                .map(savingsGoalMapper::toDTO);
    }

    /** ✅ Get all savings goals for a user */
    @Override
    public List<SavingsGoalDTO> getSavingsGoalsByUser(Long userId) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUser_UserId(userId);
        return goals.stream().map(savingsGoalMapper::toDTO).toList();
    }

    /** ✅ Update a savings goal */
    @Override
    @Transactional
    public SavingsGoalDTO updateSavingsGoal(Long id, SavingsGoalDTO savingsGoalDTO) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));

        // ✅ Prevent `deadline` from being null
        if (savingsGoalDTO.getDeadline() == null) {
            throw new RuntimeException("Deadline cannot be null! Please provide a valid date.");
        }

        savingsGoal.setGoalName(savingsGoalDTO.getGoalName());
        savingsGoal.setTargetAmount(savingsGoalDTO.getTargetAmount());
        savingsGoal.setDeadline(savingsGoalDTO.getDeadline());
        savingsGoal.setAutoSave(savingsGoalDTO.isAutoSave());
        savingsGoal.setPriorityLevel(PriorityLevel.valueOf(savingsGoalDTO.getPriorityLevel()));
        savingsGoal.setContributionFrequency(ContributionFrequency.valueOf(savingsGoalDTO.getContributionFrequency()));
        savingsGoal.setDateUpdated(LocalDate.from(LocalDateTime.now()));

        return savingsGoalMapper.toDTO(savingsGoalRepository.save(savingsGoal));
    }


    /** ✅ Delete a savings goal */
    @Override
    @Transactional
    public void deleteSavingsGoal(Long id) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        savingsGoal.setDeleted(true); // Soft delete
        savingsGoalRepository.save(savingsGoal);
    }

    /** ✅ Contribute money to a savings goal */
    @Override
    @Transactional
    public SavingsGoalDTO contributeToSavings(Long savingsGoalId, BigDecimal amount) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(savingsGoalId)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));

        savingsGoal.setCurrentAmount(savingsGoal.getCurrentAmount().add(amount));
        savingsGoal.setDateUpdated(LocalDate.from(LocalDateTime.now()));

        // ✅ If goal is reached, update status
        if (savingsGoal.getCurrentAmount().compareTo(savingsGoal.getTargetAmount()) >= 0) {
            savingsGoal.setStatus(SavingsGoalStatus.COMPLETED);
        }

        return savingsGoalMapper.toDTO(savingsGoalRepository.save(savingsGoal));
    }


    /** ✅ Automatically contributes to all `autoSave` enabled savings goals */
    @Scheduled(cron = "0 0 12 * * ?") // Runs daily at noon
    @Transactional
    public void processAutoSaveContributions() {
        List<SavingsGoal> autoSaveGoals = savingsGoalRepository.findByAutoSaveTrue();

        for (SavingsGoal goal : autoSaveGoals) {
            Account linkedAccount = accountRepository.findByUser_UserId(goal.getUser().getUserId())
                    .stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No account found for user"));

            BigDecimal contributionAmount = calculateAutoSaveAmount(goal);

            if (linkedAccount.getBalance().compareTo(contributionAmount) >= 0) {
                linkedAccount.setBalance(linkedAccount.getBalance().subtract(contributionAmount));
                goal.setCurrentAmount(goal.getCurrentAmount().add(contributionAmount));

                if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
                    goal.setStatus(SavingsGoalStatus.COMPLETED);
                }

                savingsGoalRepository.save(goal);
                accountRepository.save(linkedAccount);

                // ✅ Send confirmation email
                emailService.sendSavingsContributionEmail(goal.getUser().getUserId(), goal.getGoalName(), contributionAmount);
            }
        }
    }

    /** ✅ Determine how much to contribute based on goal settings */
    private BigDecimal calculateAutoSaveAmount(SavingsGoal goal) {
        switch (goal.getContributionFrequency()) {
            case DAILY:
                return goal.getTargetAmount().divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
            case WEEKLY:
                return goal.getTargetAmount().divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
            case MONTHLY:
            default:
                return goal.getTargetAmount();
        }
    }

    @Scheduled(cron = "0 0 9 * * ?") // Runs daily at 9 AM
    public void sendSavingsGoalReminders() {
        List<SavingsGoal> goals = savingsGoalRepository.findByStatus(SavingsGoalStatus.ACTIVE);

        for (SavingsGoal goal : goals) {
            BigDecimal progress = goal.getCurrentAmount().divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            if (progress.compareTo(BigDecimal.valueOf(80)) >= 0) {
                scheduledEmailService.sendSavingsGoalReminders();
            }
        }
    }

    @Scheduled(cron = "0 0 12 * * ?") // Runs daily at noon
    public void processRecurringDeposits() {
        List<SavingsGoal> recurringGoals = savingsGoalRepository.findByAutoSaveTrue();

        for (SavingsGoal goal : recurringGoals) {
            LocalDate lastDepositDate = goal.getLastDepositDate();
            LocalDate today = LocalDate.now();

            boolean shouldDeposit = switch (goal.getContributionFrequency()) {
                case DAILY -> true;
                case WEEKLY -> lastDepositDate.plusWeeks(1).isBefore(today);
                case BIWEEKLY -> lastDepositDate.plusWeeks(2).isBefore(today);
                case MONTHLY -> lastDepositDate.plusMonths(1).isBefore(today);
                default -> false;
            };

            if (shouldDeposit) {
                Account linkedAccount = accountRepository.findByUser_UserId(goal.getUser().getUserId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No account found for user"));

                BigDecimal contributionAmount = calculateAutoSaveAmount(goal);

                if (linkedAccount.getBalance().compareTo(contributionAmount) >= 0) {
                    linkedAccount.setBalance(linkedAccount.getBalance().subtract(contributionAmount));
                    goal.setCurrentAmount(goal.getCurrentAmount().add(contributionAmount));
                    goal.setLastDepositDate(today);

                    if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
                        goal.setStatus(SavingsGoalStatus.COMPLETED);
                    }

                    savingsGoalRepository.save(goal);
                    accountRepository.save(linkedAccount);
                    emailService.sendSavingsContributionEmail(goal.getUser().getUserId(), goal.getGoalName(), contributionAmount);
                }
            }
        }
    }


}

