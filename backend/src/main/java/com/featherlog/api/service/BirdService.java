package com.featherlog.api.service;

import com.featherlog.api.dto.BirdRequest;
import com.featherlog.api.dto.BirdResponse;
import com.featherlog.api.dto.PageResponse;
import com.featherlog.api.exception.ResourceNotFoundException;
import com.featherlog.api.model.Bird;
import com.featherlog.api.model.ConservationStatus;
import com.featherlog.api.repository.BirdRepository;
import com.featherlog.api.repository.BirdSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BirdService {

    private final BirdRepository birdRepository;

    public PageResponse<BirdResponse> findAll(String search, String family, ConservationStatus status, Pageable pageable) {
        Specification<Bird> spec = Specification.where(BirdSpecifications.search(search))
                .and(BirdSpecifications.hasFamily(family))
                .and(BirdSpecifications.hasConservationStatus(status));

        return PageResponse.from(birdRepository.findAll(spec, pageable).map(BirdResponse::from));
    }

    public BirdResponse findById(Long id) {
        return BirdResponse.from(getBirdOrThrow(id));
    }

    public List<String> findFamilies() {
        return birdRepository.findDistinctFamilies();
    }

    @Transactional
    public BirdResponse create(BirdRequest request) {
        Bird bird = Bird.builder()
                .commonName(request.commonName())
                .scientificName(request.scientificName())
                .family(request.family())
                .conservationStatus(request.conservationStatus())
                .sizeCategory(request.sizeCategory())
                .averageLengthCm(request.averageLengthCm())
                .averageWeightGrams(request.averageWeightGrams())
                .habitat(request.habitat())
                .diet(request.diet())
                .migratory(request.migratory())
                .description(request.description())
                .icon(request.icon())
                .colorHex(request.colorHex())
                .build();
        return BirdResponse.from(birdRepository.save(bird));
    }

    @Transactional
    public BirdResponse update(Long id, BirdRequest request) {
        Bird bird = getBirdOrThrow(id);
        bird.setCommonName(request.commonName());
        bird.setScientificName(request.scientificName());
        bird.setFamily(request.family());
        bird.setConservationStatus(request.conservationStatus());
        bird.setSizeCategory(request.sizeCategory());
        bird.setAverageLengthCm(request.averageLengthCm());
        bird.setAverageWeightGrams(request.averageWeightGrams());
        bird.setHabitat(request.habitat());
        bird.setDiet(request.diet());
        bird.setMigratory(request.migratory());
        bird.setDescription(request.description());
        bird.setIcon(request.icon());
        bird.setColorHex(request.colorHex());
        return BirdResponse.from(bird);
    }

    @Transactional
    public void delete(Long id) {
        Bird bird = getBirdOrThrow(id);
        birdRepository.delete(bird);
    }

    private Bird getBirdOrThrow(Long id) {
        return birdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bird not found with id: " + id));
    }
}
