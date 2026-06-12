package com.featherlog.api.dto;

import com.featherlog.api.model.ConservationStatus;

import java.util.List;

public record ConservationBreakdownResponse(
        List<StatusCount> statuses
) {
    public record StatusCount(ConservationStatus status, long speciesCount, long sightingCount) {
    }
}
