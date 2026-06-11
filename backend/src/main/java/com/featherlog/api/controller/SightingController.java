package com.featherlog.api.controller;

import com.featherlog.api.dto.PageResponse;
import com.featherlog.api.dto.SightingRequest;
import com.featherlog.api.dto.SightingResponse;
import com.featherlog.api.service.SightingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sightings")
@RequiredArgsConstructor
public class SightingController {

    private final SightingService sightingService;

    @GetMapping
    public PageResponse<SightingResponse> getSightings(
            @RequestParam(required = false) Long birdId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "sightingDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return sightingService.findAll(birdId, locationId, fromDate, toDate, favorite, search, pageable);
    }

    @GetMapping("/{id}")
    public SightingResponse getSighting(@PathVariable Long id) {
        return sightingService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SightingResponse createSighting(@Valid @RequestBody SightingRequest request) {
        return sightingService.create(request);
    }

    @PutMapping("/{id}")
    public SightingResponse updateSighting(@PathVariable Long id, @Valid @RequestBody SightingRequest request) {
        return sightingService.update(id, request);
    }

    @PatchMapping("/{id}/favorite")
    public SightingResponse toggleFavorite(@PathVariable Long id) {
        return sightingService.toggleFavorite(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSighting(@PathVariable Long id) {
        sightingService.delete(id);
    }
}
