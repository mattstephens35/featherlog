package com.featherlog.api.dto;

import java.util.List;

public record TimelineResponse(
        List<MonthlyCount> months
) {
    public record MonthlyCount(String yearMonth, long sightingCount) {
    }
}
