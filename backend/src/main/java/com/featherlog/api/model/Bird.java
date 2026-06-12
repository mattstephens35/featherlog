package com.featherlog.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "birds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bird {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "common_name", nullable = false, length = 100)
    private String commonName;

    @Column(name = "scientific_name", nullable = false, length = 120, unique = true)
    private String scientificName;

    @Column(nullable = false, length = 80)
    private String family;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "conservation_status", nullable = false, length = 30)
    private ConservationStatus conservationStatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "size_category", nullable = false, length = 20)
    private SizeCategory sizeCategory;

    @Column(name = "average_length_cm", precision = 6, scale = 1)
    private BigDecimal averageLengthCm;

    @Column(name = "average_weight_grams", precision = 8, scale = 1)
    private BigDecimal averageWeightGrams;

    @Column(length = 255)
    private String habitat;

    @Column(length = 255)
    private String diet;

    @Column(nullable = false)
    private boolean migratory;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 10)
    private String icon;

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
