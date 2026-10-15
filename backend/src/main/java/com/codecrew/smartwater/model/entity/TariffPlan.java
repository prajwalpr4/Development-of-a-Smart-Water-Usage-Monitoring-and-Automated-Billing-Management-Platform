package com.codecrew.smartwater.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tariff_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "base_rate_per_kl", precision = 10, scale = 2, nullable = false)
    private BigDecimal baseRatePerKl;

    @Column(name = "tier1_threshold_kl", precision = 10, scale = 2, nullable = false)
    private BigDecimal tier1ThresholdKl;

    @Column(name = "tier2_rate_per_kl", precision = 10, scale = 2, nullable = false)
    private BigDecimal tier2RatePerKl;

    @Column(name = "tier2_threshold_kl", precision = 10, scale = 2)
    private BigDecimal tier2ThresholdKl;

    @Column(name = "tier3_rate_per_kl", precision = 10, scale = 2)
    private BigDecimal tier3RatePerKl;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}