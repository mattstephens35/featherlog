package com.featherlog.api.dto;

import com.featherlog.api.model.ConservationStatus;
import com.featherlog.api.model.SizeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BirdRequest(
        @NotBlank @Size(max = 100) String commonName,
        @NotBlank @Size(max = 120) String scientificName,
        @NotBlank @Size(max = 80) String family,
        @NotNull ConservationStatus conservationStatus,
        @NotNull SizeCategory sizeCategory,
        @PositiveOrZero BigDecimal averageLengthCm,
        @PositiveOrZero BigDecimal averageWeightGrams,
        @Size(max = 255) String habitat,
        @Size(max = 255) String diet,
        boolean migratory,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 10) String icon,
        @NotBlank @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "colorHex must be a hex color, e.g. #4A6B8A") String colorHex
) {
}
