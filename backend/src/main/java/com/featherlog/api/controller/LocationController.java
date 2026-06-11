package com.featherlog.api.controller;

import com.featherlog.api.dto.CountryOption;
import com.featherlog.api.dto.LocationRequest;
import com.featherlog.api.dto.LocationResponse;
import com.featherlog.api.dto.PageResponse;
import com.featherlog.api.model.HabitatType;
import com.featherlog.api.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public PageResponse<LocationResponse> getLocations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) HabitatType habitatType,
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return locationService.findAll(search, country, habitatType, pageable);
    }

    @GetMapping("/countries")
    public List<CountryOption> getCountries() {
        return locationService.findCountries();
    }

    @GetMapping("/{id}")
    public LocationResponse getLocation(@PathVariable Long id) {
        return locationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse createLocation(@Valid @RequestBody LocationRequest request) {
        return locationService.create(request);
    }

    @PutMapping("/{id}")
    public LocationResponse updateLocation(@PathVariable Long id, @Valid @RequestBody LocationRequest request) {
        return locationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable Long id) {
        locationService.delete(id);
    }
}
