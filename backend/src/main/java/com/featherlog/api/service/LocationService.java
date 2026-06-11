package com.featherlog.api.service;

import com.featherlog.api.dto.CountryOption;
import com.featherlog.api.dto.LocationRequest;
import com.featherlog.api.dto.LocationResponse;
import com.featherlog.api.dto.PageResponse;
import com.featherlog.api.exception.ResourceNotFoundException;
import com.featherlog.api.model.HabitatType;
import com.featherlog.api.model.Location;
import com.featherlog.api.repository.LocationRepository;
import com.featherlog.api.repository.LocationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;

    public PageResponse<LocationResponse> findAll(String search, String country, HabitatType habitatType, Pageable pageable) {
        Specification<Location> spec = Specification.where(LocationSpecifications.search(search))
                .and(LocationSpecifications.hasCountry(country))
                .and(LocationSpecifications.hasHabitatType(habitatType));

        return PageResponse.from(locationRepository.findAll(spec, pageable).map(LocationResponse::from));
    }

    public LocationResponse findById(Long id) {
        return LocationResponse.from(getLocationOrThrow(id));
    }

    public List<CountryOption> findCountries() {
        return locationRepository.findDistinctCountries();
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        Location location = Location.builder()
                .name(request.name())
                .country(request.country())
                .countryCode(request.countryCode().toLowerCase())
                .region(request.region())
                .habitatType(request.habitatType())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .bestSeason(request.bestSeason())
                .description(request.description())
                .icon(request.icon())
                .build();
        return LocationResponse.from(locationRepository.save(location));
    }

    @Transactional
    public LocationResponse update(Long id, LocationRequest request) {
        Location location = getLocationOrThrow(id);
        location.setName(request.name());
        location.setCountry(request.country());
        location.setCountryCode(request.countryCode().toLowerCase());
        location.setRegion(request.region());
        location.setHabitatType(request.habitatType());
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setBestSeason(request.bestSeason());
        location.setDescription(request.description());
        location.setIcon(request.icon());
        return LocationResponse.from(location);
    }

    @Transactional
    public void delete(Long id) {
        Location location = getLocationOrThrow(id);
        locationRepository.delete(location);
    }

    private Location getLocationOrThrow(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }
}
