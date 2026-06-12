package com.featherlog.apitests.model;

import java.time.LocalDate;
import java.util.List;

public record ActivityResponse(
        List<DayCount> days,
        int longestStreak,
        LocalDate longestStreakStart,
        LocalDate longestStreakEnd
) {
    public record DayCount(LocalDate date, long sightingCount) {
    }
}
