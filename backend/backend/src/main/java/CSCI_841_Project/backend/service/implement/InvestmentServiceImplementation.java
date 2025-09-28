package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.entity.Investment;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.InvestmentMapper;
import CSCI_841_Project.backend.repository.InvestmentRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class InvestmentServiceImplementation implements InvestmentService {

    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private InvestmentMapper investmentMapper;
    @Autowired
    private UserRepository userRepository;


    /**
     * ✅ Add a new investment.
     * Automatically sets `currentValue = amountInvested` at the beginning.
     */
    @Override
    public InvestmentDTO addInvestment(InvestmentDTO investmentDTO) {
        User user = userRepository.findById(investmentDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Investment investment = investmentMapper.toEntity(investmentDTO, user);
        // ✅ Set initial value (currentValue starts as amountInvested)
        investment.setCurrentValue(investment.getAmountInvested());
        // ✅ Calculate initial performance (0% since no change yet)
        investment.setPerformance(BigDecimal.ZERO);
        // ✅ Set last updated timestamp
        investment.setLastUpdated(LocalDateTime.now());

        investment = investmentRepository.save(investment);
        return investmentMapper.toDTO(investment);
    }


    /**
     * ✅ Update an investment (allows users to update `currentValue`).
     * Automatically recalculates performance.
     */
    @Override
    public InvestmentDTO updateInvestment(Long investmentId, InvestmentDTO investmentDTO) {
        // ✅ Find the investment
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found: " + investmentId));

        // ✅ Calculate the change ratio
        if (investmentDTO.getAmountInvested().compareTo(investment.getAmountInvested()) > 0) {
            BigDecimal increaseRatio = investmentDTO.getAmountInvested()
                    .divide(investment.getAmountInvested(), 4, RoundingMode.HALF_UP);

            investment.setCurrentValue(investment.getCurrentValue().multiply(increaseRatio));
        }
        // ✅ Update investment fields
        investment.setAssetName(investmentDTO.getAssetName());
        investment.setInvestmentType(investmentDTO.getInvestmentType());
        investment.setAmountInvested(investmentDTO.getAmountInvested());
        investment.setLastUpdated(LocalDateTime.now());

        // ✅ Recalculate performance
        if (investment.getAmountInvested().compareTo(BigDecimal.ZERO) > 0) {
            investment.setPerformance(
                    investment.getCurrentValue().subtract(investment.getAmountInvested())
                            .divide(investment.getAmountInvested(), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );
        } else {
            investment.setPerformance(BigDecimal.ZERO);
        }

        investment.setLastUpdated(LocalDateTime.now());
        // ✅ Save updated investment
        investment = investmentRepository.save(investment);
        return investmentMapper.toDTO(investment);
    }

    /**
     * ✅ Get an investment by ID.
     */
    @Override
    public InvestmentDTO getInvestmentById(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new NotFoundException("Investment not found"));
        return investmentMapper.toDTO(investment);
    }

    /**
     * ✅ Get all investments for a user.
     */
    @Override
    public List<InvestmentDTO> getInvestmentsByUser(Long userId) {
        return investmentRepository.findByUser_UserId(userId)
                .stream()
                .filter(a -> !a.isDeleted())
                .map(investmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Soft delete an investment.
     */
    @Override
    public void deleteInvestment(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new NotFoundException("Investment not found"));
        investment.setDeleted(true);
        investmentRepository.save(investment);
    }

    /**
     * ✅ Restore a deleted investment.
     */
    @Override
    public void restoreInvestment(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new NotFoundException("Investment not found"));

        if (!investment.isDeleted()) {
            throw new NotFoundException("Investment is already active.");
        }
        investment.setDeleted(false);
        investmentRepository.save(investment);
    }

    /**
     * ✅ Simulate investment growth every month (±5% fluctuation).
     * Runs on the 1st day of every month at midnight.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void simulateInvestmentGrowth() {

        List<Investment> investments = investmentRepository.findAll();

        for (Investment investment : investments) {
            // ✅ Generate a random percentage between -5% and +5%
            BigDecimal marketChange = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(-5, 5));
            BigDecimal newCurrentValue = investment.getCurrentValue()
                    .multiply(BigDecimal.ONE.add(marketChange.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));

            // ✅ Ensure currentValue never goes below investedAmount
            if (newCurrentValue.compareTo(investment.getAmountInvested()) < 0) {
                newCurrentValue = investment.getAmountInvested();
            }

            investment.setCurrentValue(newCurrentValue);

            // ✅ Update performance metric
            investment.setPerformance(
                    (newCurrentValue.subtract(investment.getAmountInvested()))
                            .divide(investment.getAmountInvested(), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );

            investment.setLastUpdated(LocalDateTime.now());
            investmentRepository.save(investment);
        }

        System.out.println("📈 Investment market simulation complete.");
    }


}
