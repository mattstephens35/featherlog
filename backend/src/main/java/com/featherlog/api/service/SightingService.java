package com.featherlog.api.service;

import com.featherlog.api.dto.PageResponse;
import com.featherlog.api.dto.SightingRequest;
import com.featherlog.api.dto.SightingResponse;
import com.featherlog.api.exception.ResourceNotFoundException;
import com.featherlog.api.model.Bird;
import com.featherlog.api.model.Location;
import com.featherlog.api.model.Sighting;
import com.featherlog.api.repository.BirdRepository;
import com.featherlog.api.repository.LocationRepository;
import com.featherlog.api.repository.SightingRepository;
import com.featherlog.api.repository.SightingSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SightingService {

    private final SightingRepository sightingRepository;
    private final BirdRepository birdRepository;
    private final LocationRepository locationRepository;

    public PageResponse<SightingResponse> findAll(Long birdId, Long locationId, LocalDate fromDate, LocalDate toDate,
                                                    Boolean favorite, String search, Pageable pageable) {
        Specification<Sighting> spec = Specification.where(SightingSpecifications.hasBird(birdId))
                .and(SightingSpecifications.hasLocation(locationId))
                .and(SightingSpecifications.dateFrom(fromDate))
                .and(SightingSpecifications.dateTo(toDate))
                .and(SightingSpecifications.isFavorite(favorite))
                .and(SightingSpecifications.search(search));

        return PageResponse.from(sightingRepository.findAll(spec, pageable).map(SightingResponse::from));
    }

    public SightingResponse findById(Long id) {
        return SightingResponse.from(getSightingOrThrow(id));
    }

    @Transactional
    public SightingResponse create(SightingRequest request) {
        Bird bird = getBirdOrThrow(request.birdId());
        Location location = getLocationOrThrow(request.locationId());

        Sighting sighting = Sighting.builder()
                .bird(bird)
                .location(location)
                .sightingDate(request.sightingDate())
                .sightingTime(request.sightingTime())
                .quantity(request.quantity())
                .observerName(request.observerName())
                .weatherCondition(request.weatherCondition())
                .notes(request.notes())
                .favorite(request.favorite())
                .build();

        return SightingResponse.from(sightingRepository.save(sighting));
    }

    @Transactional
    public SightingResponse update(Long id, SightingRequest request) {
        Sighting sighting = getSightingOrThrow(id);
        Bird bird = getBirdOrThrow(request.birdId());
        Location location = getLocationOrThrow(request.locationId());

        sighting.setBird(bird);
        sighting.setLocation(location);
        sighting.setSightingDate(request.sightingDate());
        sighting.setSightingTime(request.sightingTime());
        sighting.setQuantity(request.quantity());
        sighting.setObserverName(request.observerName());
        sighting.setWeatherCondition(request.weatherCondition());
        sighting.setNotes(request.notes());
        sighting.setFavorite(request.favorite());

        return SightingResponse.from(sighting);
    }

    @Transactional
    public SightingResponse toggleFavorite(Long id) {
        Sighting sighting = getSightingOrThrow(id);
        sighting.setFavorite(!sighting.isFavorite());
        return SightingResponse.from(sighting);
    }

    @Transactional
    public void delete(Long id) {
        Sighting sighting = getSightingOrThrow(id);
        sightingRepository.delete(sighting);
    }

    private Sighting getSightingOrThrow(Long id) {
        return sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));
    }

    private Bird getBirdOrThrow(Long id) {
        return birdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bird not found with id: " + id));
    }

    private Location getLocationOrThrow(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }
}
