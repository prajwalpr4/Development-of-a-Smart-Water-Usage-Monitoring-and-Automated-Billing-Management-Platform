package com.codecrew.smartwater.model.entity;

import com.codecrew.smartwater.model.enums.MeterStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "water_meters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_number", nullable = false, unique = true)
    private String meterNumber;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MeterStatus status = MeterStatus.ACTIVE;

    @Column(name = "is_virtual", nullable = false)
    @Builder.Default
    private Boolean isVirtual = true;

    @Column(name = "last_reading_value", precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal lastReadingValue = BigDecimal.ZERO;

    @Column(name = "last_reading_date")
    private LocalDate lastReadingDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "household_id", nullable = false, unique = true)
    private Household household;
}