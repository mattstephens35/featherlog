package com.featherlog.apitests.model;

import java.util.List;

public record StatsSummaryResponse(
        long totalSightings,
        long speciesRecorded,
        long locationsVisited,
        long totalSpeciesCatalog,
        long totalLocationsCatalog,
        long sightingsThisMonth,
        List<SightingResponse> recentSightings,
        List<TopBird> topBirds,
        List<TopLocation> topLocations
) {
    public record TopBird(Long birdId, String commonName, String icon, String colorHex, long sightingCount) {
    }

    public record TopLocation(Long locationId, String name, String countryCode, String icon, long sightingCount) {
    }
}
