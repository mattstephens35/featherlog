package com.featherlog.api.repository;

import com.featherlog.api.model.Sighting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SightingRepository extends JpaRepository<Sighting, Long>, JpaSpecificationExecutor<Sighting> {

    List<Sighting> findTop5ByOrderBySightingDateDescIdDesc();

    @Query("SELECT COUNT(DISTINCT s.bird.id) FROM Sighting s")
    long countDistinctBirds();

    @Query("SELECT COUNT(DISTINCT s.location.id) FROM Sighting s")
    long countDistinctLocations();

    long countBySightingDateBetween(LocalDate start, LocalDate end);

    @Query("""
            SELECT s.bird.id, s.bird.commonName, s.bird.icon, s.bird.colorHex, COUNT(s)
            FROM Sighting s
            GROUP BY s.bird.id, s.bird.commonName, s.bird.icon, s.bird.colorHex
            ORDER BY COUNT(s) DESC, s.bird.commonName ASC
            """)
    List<Object[]> findTopBirds(Pageable pageable);

    @Query("""
            SELECT s.location.id, s.location.name, s.location.countryCode, s.location.icon, COUNT(s)
            FROM Sighting s
            GROUP BY s.location.id, s.location.name, s.location.countryCode, s.location.icon
            ORDER BY COUNT(s) DESC, s.location.name ASC
            """)
    List<Object[]> findTopLocations(Pageable pageable);
}
