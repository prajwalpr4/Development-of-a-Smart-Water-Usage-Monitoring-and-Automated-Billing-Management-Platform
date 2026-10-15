package com.codecrew.smartwater.model.entity;

import com.codecrew.smartwater.model.enums.ReadingSource;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "water_usage_logs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_meter_reading_date", columnNames = {"meter_id", "reading_date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private WaterMeter waterMeter;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "current_reading", precision = 12, scale = 3, nullable = false)
    private BigDecimal currentReading;

    @Column(name = "consumption", precision = 12, scale = 3, nullable = false)
    private BigDecimal consumption;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_source", nullable = false)
    private ReadingSource readingSource;

    @Column(name = "is_simulated", nullable = false)
    @Builder.Default
    private Boolean isSimulated = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}