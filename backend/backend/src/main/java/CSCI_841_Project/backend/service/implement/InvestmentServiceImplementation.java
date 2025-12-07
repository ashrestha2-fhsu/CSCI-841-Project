package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.InvestmentDTO;
import CSCI_841_Project.backend.dto.InvestmentDetailsDTO;
import CSCI_841_Project.backend.dto.InvestmentHistoryDTO;
import CSCI_841_Project.backend.dto.InvestmentReportDTO;
import CSCI_841_Project.backend.entity.Investment;
import CSCI_841_Project.backend.entity.InvestmentHistory;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.InvestmentMapper;
import CSCI_841_Project.backend.repository.InvestmentHistoryRepository;
import CSCI_841_Project.backend.repository.InvestmentRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.InvestmentHistoryService;
import CSCI_841_Project.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private InvestmentHistoryRepository investmentHistoryRepository;
    @Autowired
    private InvestmentHistoryService investmentHistoryService;


    /**
     * ✅ Add a new investment.
     * Automatically sets `currentValue = amountInvested` at the beginning.
     */
    @Override
    public InvestmentDTO addInvestment(InvestmentDTO dto) {

        User user = getCurrentUser();

        // ✅ Map DTO to entity with that user
        Investment investment = investmentMapper.toEntity(dto, user);

        // ✅ Optional: calculate currentValue from symbol + quantity
        if (investment.getAssetSymbol() != null && investment.getQuantity() != null) {
            BigDecimal marketPrice = fetchMarketPrice(String.valueOf(investment.getAssetSymbol()));
            BigDecimal currentValue = marketPrice.multiply(investment.getQuantity());
            investment.setCurrentValue(currentValue);
        } else {
            investment.setCurrentValue(investment.getTotalAmountInvested());
        }

        investment.setPerformance(BigDecimal.ZERO);
        investment.setLastUpdated(LocalDateTime.now());

        // ✅ Save investment first
        investment = investmentRepository.save(investment);

        // ✅ Record the initial investment history snapshot
        investmentHistoryService.recordInvestmentHistory(
                investment.getInvestmentId(),
                investment.getCurrentValue()
        );

        return investmentMapper.toDTO(investment);
    }

    private User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private BigDecimal fetchMarketPrice(String symbol) {
        // Temporary mock logic (you can replace with API integration)
        if (symbol.equalsIgnoreCase("AAPL")) return BigDecimal.valueOf(150);
        if (symbol.equalsIgnoreCase("BTC")) return BigDecimal.valueOf(27000);
        return BigDecimal.valueOf(100); // default price
    }


    /**
     * ✅ Update an investment (allows users to update `currentValue`).
     * Automatically recalculates performance.
     */
    @Override
    public InvestmentDTO updateInvestment(Long investmentId, InvestmentDTO investmentDTO, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found: " + investmentId));

        // 🔐 Ensure this investment belongs to the logged-in user
        if (!investment.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You do not own this investment.");
        }

        // ✅ Keep your existing logic
        if (investmentDTO.getTotalAmountInvested().compareTo(investment.getTotalAmountInvested()) > 0) {
            BigDecimal increaseRatio = investmentDTO.getTotalAmountInvested()
                    .divide(investment.getTotalAmountInvested(), 4, RoundingMode.HALF_UP);
            investment.setCurrentValue(investment.getCurrentValue().multiply(increaseRatio));
        }

        investment.setAssetName(investmentDTO.getAssetName());
        investment.setInvestmentType(investmentDTO.getInvestmentType());
        investment.setTotalAmountInvested(investmentDTO.getTotalAmountInvested());
        investment.setCurrentValue(investmentDTO.getCurrentValue());
        investment.setQuantity(investmentDTO.getQuantity());
        investment.setLastUpdated(LocalDateTime.now());

        if (investment.getTotalAmountInvested().compareTo(BigDecimal.ZERO) > 0) {
            investment.setPerformance(
                    investment.getCurrentValue()
                            .subtract(investment.getTotalAmountInvested())
                            .divide(investment.getTotalAmountInvested(), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );
        } else {
            investment.setPerformance(BigDecimal.ZERO);
        }

        investment = investmentRepository.save(investment);

        // ✅ Record new investment history snapshot after update
        BigDecimal previousValue = investment.getCurrentValue();
        BigDecimal newValue = investmentDTO.getCurrentValue();

        if (newValue != null && previousValue.compareTo(newValue) != 0) {
            investmentHistoryService.recordInvestmentHistory(
                    investment.getInvestmentId(),
                    newValue
            );
        }

//        investmentHistoryService.recordInvestmentHistory(
//                investment.getInvestmentId(),
//                investment.getCurrentValue()
//        );

        return investmentMapper.toDTO(investment);
    }


// RE-INVEST TO A CURRENT INVESTMENT METHOD  ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
@Override
@Transactional
public InvestmentDTO reinvest(Long investmentId, BigDecimal amountInvested, BigDecimal reinvestedQuantity, String username) {
    User user = userRepository.findByUserName(username)
            .orElseThrow(() -> new NotFoundException("User not found"));

    Investment investment = investmentRepository.findById(investmentId)
            .orElseThrow(() -> new NotFoundException("Investment not found"));

    if (!investment.getUser().getUserId().equals(user.getUserId())) {
        throw new AccessDeniedException("You do not own this investment.");
    }

    // ✅ Safely handle null values
    BigDecimal oldTotalInvested = investment.getTotalAmountInvested() != null
            ? investment.getTotalAmountInvested()
            : BigDecimal.ZERO;

    BigDecimal safeAmountInvested = amountInvested != null ? amountInvested : BigDecimal.ZERO;
    BigDecimal newTotalInvested = oldTotalInvested.add(safeAmountInvested);

    BigDecimal oldQuantity = investment.getQuantity() != null
            ? investment.getQuantity()
            : BigDecimal.ZERO;

    BigDecimal safeReinvestedQuantity = reinvestedQuantity != null ? reinvestedQuantity : BigDecimal.ZERO;
    BigDecimal newQuantity = oldQuantity.add(safeReinvestedQuantity);
    System.out.println("Updated quantity: " + newQuantity);


    // ✅ Recalculate values
    BigDecimal marketPrice = fetchMarketPrice(String.valueOf(investment.getAssetSymbol()));
    BigDecimal newCurrentValue = marketPrice.multiply(newQuantity);

    BigDecimal returnsGenerated = newCurrentValue.subtract(newTotalInvested);
    BigDecimal performance = newTotalInvested.compareTo(BigDecimal.ZERO) > 0
            ? returnsGenerated.divide(newTotalInvested, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

    // ✅ Update investment
    investment.setTotalAmountInvested(newTotalInvested);
    investment.setQuantity(newQuantity);
    investment.setCurrentValue(newCurrentValue);
    investment.setPerformance(performance);
    investment.setLastUpdated(LocalDateTime.now());

    investmentRepository.save(investment);

    // ✅ Log to investment history
    InvestmentHistory history = new InvestmentHistory();
    history.setInvestment(investment);
    history.setAmountInvested(safeAmountInvested);
    history.setTotalAmountInvested(newTotalInvested);
    history.setCurrentValue(newCurrentValue);
    history.setPerformance(performance);
    history.setReturnsGenerated(returnsGenerated);
    history.setRecordedAt(LocalDateTime.now());

    investmentHistoryRepository.save(history);

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
    @Transactional
    public void deleteInvestment(Long investmentId) {
        if (!investmentRepository.existsById(investmentId)) {
            throw new NotFoundException("Investment not found");
        }
        investmentRepository.softDeleteById(investmentId);
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
            if (newCurrentValue.compareTo(investment.getTotalAmountInvested()) < 0) {
                newCurrentValue = investment.getTotalAmountInvested();
            }

            investment.setCurrentValue(newCurrentValue);

            // ✅ Update performance metric
            investment.setPerformance(
                    (newCurrentValue.subtract(investment.getTotalAmountInvested()))
                            .divide(investment.getTotalAmountInvested(), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );

            investment.setLastUpdated(LocalDateTime.now());
            investmentRepository.save(investment);
        }

        System.out.println("📈 Investment market simulation complete.");
    }

    @Override
    public InvestmentReportDTO getInvestmentReport(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalReturns = investmentRepository.getTotalInvestmentsByUserId(userId,
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        List<Investment> investments = investmentRepository.findByUser_UserId(userId)
                .stream().filter(a -> !a.isDeleted()).toList();

        BigDecimal totalInvested = investments.stream()
                .map(Investment::getTotalAmountInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentValue = investments.stream()
                .map(Investment::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<InvestmentDetailsDTO> details = investments.stream()
                .map(investmentMapper::toDetailsDTO)
                .toList();

        InvestmentReportDTO report = new InvestmentReportDTO();
        report.setUserId(userId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalInvested(totalInvested);
        report.setTotalCurrentValue(totalCurrentValue);
        report.setTotalReturns(totalReturns);
        report.setInvestments(details);
        return report;
    }


    @Override
    public List<InvestmentHistoryDTO> getInvestmentHistory(Long investmentId) {
        return investmentHistoryRepository.findByInvestment_InvestmentId(investmentId).stream()
                .map(investmentMapper::toHistoryDTO)
                .toList();
    }

    @Override
    public Optional<InvestmentDTO> findBySymbol(Long userId, String symbol) {
        return investmentRepository.findByUser_UserIdAndAssetSymbol(userId, symbol)
                .map(investmentMapper::toDTO);
    }



}
