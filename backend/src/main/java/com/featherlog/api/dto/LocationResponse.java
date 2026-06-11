package com.featherlog.api.dto;

import com.featherlog.api.model.HabitatType;
import com.featherlog.api.model.Location;

import java.math.BigDecimal;

public record LocationResponse(
        Long id,
        String name,
        String country,
        String countryCode,
        String region,
        HabitatType habitatType,
        BigDecimal latitude,
        BigDecimal longitude,
        String bestSeason,
        String description,
        String icon
) {
    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getCountry(),
                location.getCountryCode(),
                location.getRegion(),
                location.getHabitatType(),
                location.getLatitude(),
                location.getLongitude(),
                location.getBestSeason(),
                location.getDescription(),
                location.getIcon()
        );
    }
}
