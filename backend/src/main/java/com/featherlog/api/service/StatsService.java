package com.featherlog.api.service;

import com.featherlog.api.dto.ActivityResponse;
import com.featherlog.api.dto.ConservationBreakdownResponse;
import com.featherlog.api.dto.LifersResponse;
import com.featherlog.api.dto.SightingResponse;
import com.featherlog.api.dto.StatsSummaryResponse;
import com.featherlog.api.dto.TimelineResponse;
import com.featherlog.api.model.Bird;
import com.featherlog.api.model.ConservationStatus;
import com.featherlog.api.model.Sighting;
import com.featherlog.api.repository.BirdRepository;
import com.featherlog.api.repository.LocationRepository;
import com.featherlog.api.repository.SightingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private static final int TOP_N = 5;

    private final SightingRepository sightingRepository;
    private final BirdRepository birdRepository;
    private final LocationRepository locationRepository;

    public StatsSummaryResponse getSummary() {
        long totalSightings = sightingRepository.count();
        long speciesRecorded = sightingRepository.countDistinctBirds();
        long locationsVisited = sightingRepository.countDistinctLocations();
        long totalSpeciesCatalog = birdRepository.count();
        long totalLocationsCatalog = locationRepository.count();

        YearMonth currentMonth = YearMonth.now();
        long sightingsThisMonth = sightingRepository.countBySightingDateBetween(
                currentMonth.atDay(1), currentMonth.atEndOfMonth());

        List<SightingResponse> recentSightings = sightingRepository.findTop5ByOrderBySightingDateDescIdDesc()
                .stream()
                .map(SightingResponse::from)
                .toList();

        List<StatsSummaryResponse.TopBird> topBirds = sightingRepository.findTopBirds(PageRequest.of(0, TOP_N))
                .stream()
                .map(row -> new StatsSummaryResponse.TopBird(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (Long) row[4]))
                .toList();

        List<StatsSummaryResponse.TopLocation> topLocations = sightingRepository.findTopLocations(PageRequest.of(0, TOP_N))
                .stream()
                .map(row -> new StatsSummaryResponse.TopLocation(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (Long) row[4]))
                .toList();

        return new StatsSummaryResponse(
                totalSightings,
                speciesRecorded,
                locationsVisited,
                totalSpeciesCatalog,
                totalLocationsCatalog,
                sightingsThisMonth,
                recentSightings,
                topBirds,
                topLocations
        );
    }

    public TimelineResponse getTimeline() {
        List<LocalDate> dates = sightingRepository.findAllSightingDates();

        Map<YearMonth, Long> counts = dates.stream()
                .collect(Collectors.groupingBy(YearMonth::from, TreeMap::new, Collectors.counting()));

        List<TimelineResponse.MonthlyCount> months = counts.entrySet().stream()
                .map(e -> new TimelineResponse.MonthlyCount(e.getKey().toString(), e.getValue()))
                .toList();

        return new TimelineResponse(months);
    }

    public ConservationBreakdownResponse getConservationBreakdown() {
        Map<ConservationStatus, Long> speciesCounts = birdRepository.findAll().stream()
                .collect(Collectors.groupingBy(Bird::getConservationStatus, Collectors.counting()));

        Map<ConservationStatus, Long> sightingCounts = sightingRepository.findAllSightingConservationStatuses().stream()
                .collect(Collectors.groupingBy(status -> status, Collectors.counting()));

        List<ConservationBreakdownResponse.StatusCount> statuses = Arrays.stream(ConservationStatus.values())
                .map(status -> new ConservationBreakdownResponse.StatusCount(
                        status,
                        speciesCounts.getOrDefault(status, 0L),
                        sightingCounts.getOrDefault(status, 0L)))
                .toList();

        return new ConservationBreakdownResponse(statuses);
    }

    public LifersResponse getLifers() {
        List<Sighting> sightings = sightingRepository.findAllByOrderBySightingDateAscIdAsc();

        Map<Long, Sighting> firstSightingByBird = new LinkedHashMap<>();
        for (Sighting sighting : sightings) {
            firstSightingByBird.putIfAbsent(sighting.getBird().getId(), sighting);
        }

        List<LifersResponse.Lifer> lifers = firstSightingByBird.values().stream()
                .sorted(Comparator.comparing(Sighting::getSightingDate).thenComparing(Sighting::getId))
                .map(s -> new LifersResponse.Lifer(
                        SightingResponse.BirdSummary.from(s.getBird()),
                        s.getSightingDate(),
                        SightingResponse.LocationSummary.from(s.getLocation())))
                .toList();

        return new LifersResponse(lifers);
    }

    public ActivityResponse getActivity() {
        List<LocalDate> dates = sightingRepository.findAllSightingDates();

        Map<LocalDate, Long> countsByDate = dates.stream()
                .collect(Collectors.groupingBy(d -> d, TreeMap::new, Collectors.counting()));

        List<ActivityResponse.DayCount> days = countsByDate.entrySet().stream()
                .map(e -> new ActivityResponse.DayCount(e.getKey(), e.getValue()))
                .toList();

        int longestStreak = 0;
        LocalDate longestStart = null;
        LocalDate longestEnd = null;

        int currentStreak = 0;
        LocalDate currentStart = null;
        LocalDate previousDate = null;

        for (LocalDate date : countsByDate.keySet()) {
            if (previousDate != null && date.equals(previousDate.plusDays(1))) {
                currentStreak++;
            } else {
                currentStreak = 1;
                currentStart = date;
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
                longestStart = currentStart;
                longestEnd = date;
            }
            previousDate = date;
        }

        return new ActivityResponse(days, longestStreak, longestStart, longestEnd);
    }
}
