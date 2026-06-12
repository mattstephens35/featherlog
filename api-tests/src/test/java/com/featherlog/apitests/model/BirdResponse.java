package com.featherlog.apitests.model;

import java.math.BigDecimal;

public record BirdResponse(
        Long id,
        String commonName,
        String scientificName,
        String family,
        ConservationStatus conservationStatus,
        SizeCategory sizeCategory,
        BigDecimal averageLengthCm,
        BigDecimal averageWeightGrams,
        String habitat,
        String diet,
        boolean migratory,
        String description,
        String icon,
        String colorHex
) {
}
