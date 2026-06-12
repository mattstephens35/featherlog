package com.featherlog.apitests.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record SightingResponse(
        Long id,
        BirdSummary bird,
        LocationSummary location,
        LocalDate sightingDate,
        LocalTime sightingTime,
        Integer quantity,
        String observerName,
        WeatherCondition weatherCondition,
        String notes,
        boolean favorite,
        Instant createdAt,
        Instant updatedAt
) {
    public record BirdSummary(Long id, String commonName, String scientificName, String icon, String colorHex) {
    }

    public record LocationSummary(Long id, String name, String country, String countryCode, String icon) {
    }
}
