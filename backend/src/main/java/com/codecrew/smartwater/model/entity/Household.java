package com.codecrew.smartwater.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "households",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"apartment_id", "flat_number"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flat_number", nullable = false)
    private String flatNumber;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "area_sq_ft", precision = 10, scale = 2, nullable = false)
    private BigDecimal areaSqFt;

    @Column(name = "occupants_count", nullable = false)
    private Integer occupantsCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", unique = true)
    private User resident;

    @OneToOne(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private WaterMeter waterMeter;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}