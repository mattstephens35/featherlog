package com.featherlog.api.dto;

import com.featherlog.api.model.Bird;
import com.featherlog.api.model.Location;
import com.featherlog.api.model.Sighting;
import com.featherlog.api.model.WeatherCondition;

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
    public static SightingResponse from(Sighting sighting) {
        return new SightingResponse(
                sighting.getId(),
                BirdSummary.from(sighting.getBird()),
                LocationSummary.from(sighting.getLocation()),
                sighting.getSightingDate(),
                sighting.getSightingTime(),
                sighting.getQuantity(),
                sighting.getObserverName(),
                sighting.getWeatherCondition(),
                sighting.getNotes(),
                sighting.isFavorite(),
                sighting.getCreatedAt(),
                sighting.getUpdatedAt()
        );
    }

    public record BirdSummary(Long id, String commonName, String scientificName, String icon, String colorHex) {
        public static BirdSummary from(Bird bird) {
            return new BirdSummary(bird.getId(), bird.getCommonName(), bird.getScientificName(), bird.getIcon(), bird.getColorHex());
        }
    }

    public record LocationSummary(Long id, String name, String country, String countryCode, String icon) {
        public static LocationSummary from(Location location) {
            return new LocationSummary(location.getId(), location.getName(), location.getCountry(), location.getCountryCode(), location.getIcon());
        }
    }
}
