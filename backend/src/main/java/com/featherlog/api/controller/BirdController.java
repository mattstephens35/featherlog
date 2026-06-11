package com.featherlog.api.controller;

import com.featherlog.api.dto.BirdRequest;
import com.featherlog.api.dto.BirdResponse;
import com.featherlog.api.dto.PageResponse;
import com.featherlog.api.model.ConservationStatus;
import com.featherlog.api.service.BirdService;
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
@RequestMapping("/api/birds")
@RequiredArgsConstructor
public class BirdController {

    private final BirdService birdService;

    @GetMapping
    public PageResponse<BirdResponse> getBirds(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String family,
            @RequestParam(required = false) ConservationStatus conservationStatus,
            @PageableDefault(size = 12, sort = "commonName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return birdService.findAll(search, family, conservationStatus, pageable);
    }

    @GetMapping("/families")
    public List<String> getFamilies() {
        return birdService.findFamilies();
    }

    @GetMapping("/{id}")
    public BirdResponse getBird(@PathVariable Long id) {
        return birdService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BirdResponse createBird(@Valid @RequestBody BirdRequest request) {
        return birdService.create(request);
    }

    @PutMapping("/{id}")
    public BirdResponse updateBird(@PathVariable Long id, @Valid @RequestBody BirdRequest request) {
        return birdService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBird(@PathVariable Long id) {
        birdService.delete(id);
    }
}
