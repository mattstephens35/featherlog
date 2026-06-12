package com.featherlog.api.dto;

import java.time.LocalDate;
import java.util.List;

public record LifersResponse(
        List<Lifer> lifers
) {
    public record Lifer(
            SightingResponse.BirdSummary bird,
            LocalDate firstSeenDate,
            SightingResponse.LocationSummary firstSeenLocation
    ) {
    }
}
