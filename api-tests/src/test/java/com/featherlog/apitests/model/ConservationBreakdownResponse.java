package com.featherlog.apitests.model;

import java.util.List;

public record ConservationBreakdownResponse(
        List<StatusCount> statuses
) {
    public record StatusCount(ConservationStatus status, long speciesCount, long sightingCount) {
    }
}
