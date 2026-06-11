package com.featherlog.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sightings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bird_id", nullable = false)
    private Bird bird;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "sighting_date", nullable = false)
    private LocalDate sightingDate;

    @Column(name = "sighting_time")
    private LocalTime sightingTime;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "observer_name", nullable = false, length = 100)
    private String observerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_condition", length = 20)
    private WeatherCondition weatherCondition;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private boolean favorite;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
