package com.featherlog.api.service;

import com.featherlog.api.dto.SightingResponse;
import com.featherlog.api.dto.StatsSummaryResponse;
import com.featherlog.api.repository.BirdRepository;
import com.featherlog.api.repository.LocationRepository;
import com.featherlog.api.repository.SightingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

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
}
