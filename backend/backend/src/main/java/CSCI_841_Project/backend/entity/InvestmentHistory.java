package CSCI_841_Project.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "investment_history")
public class InvestmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_investment_history_investment"))
    private Investment investment;

    @Column(name = "current_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "performance", precision = 5, scale = 2)
    private BigDecimal performance;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "returns_generated", precision = 15, scale = 2)
    private BigDecimal returnsGenerated;


    // ✅ Automatically set recordedAt before persisting
    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}
