package com.featherlog.api.dto;

import com.featherlog.api.model.Bird;
import com.featherlog.api.model.ConservationStatus;
import com.featherlog.api.model.SizeCategory;

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
    public static BirdResponse from(Bird bird) {
        return new BirdResponse(
                bird.getId(),
                bird.getCommonName(),
                bird.getScientificName(),
                bird.getFamily(),
                bird.getConservationStatus(),
                bird.getSizeCategory(),
                bird.getAverageLengthCm(),
                bird.getAverageWeightGrams(),
                bird.getHabitat(),
                bird.getDiet(),
                bird.isMigratory(),
                bird.getDescription(),
                bird.getIcon(),
                bird.getColorHex()
        );
    }
}
