package com.featherlog.apitests.model;

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
}
