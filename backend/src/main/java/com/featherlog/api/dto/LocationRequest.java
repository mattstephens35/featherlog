package com.featherlog.api.dto;

import com.featherlog.api.model.HabitatType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record LocationRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 100) String country,
        @NotBlank @Pattern(regexp = "^[a-zA-Z]{2}$", message = "countryCode must be a 2-letter ISO country code") String countryCode,
        @Size(max = 100) String region,
        @NotNull HabitatType habitatType,
        @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
        @Size(max = 120) String bestSeason,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 10) String icon
) {
}
