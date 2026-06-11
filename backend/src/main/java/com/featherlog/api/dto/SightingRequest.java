package com.featherlog.api.dto;

import com.featherlog.api.model.WeatherCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record SightingRequest(
        @NotNull Long birdId,
        @NotNull Long locationId,
        @NotNull @PastOrPresent(message = "sightingDate cannot be in the future") LocalDate sightingDate,
        LocalTime sightingTime,
        @NotNull @Min(1) @Max(10000) Integer quantity,
        @NotBlank @Size(max = 100) String observerName,
        WeatherCondition weatherCondition,
        @Size(max = 2000) String notes,
        boolean favorite
) {
}
