package com.featherlog.api.repository;

import com.featherlog.api.model.Sighting;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class SightingSpecifications {

    private SightingSpecifications() {
    }

    public static Specification<Sighting> hasBird(Long birdId) {
        return (root, query, cb) -> {
            if (birdId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("bird").get("id"), birdId);
        };
    }

    public static Specification<Sighting> hasLocation(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("location").get("id"), locationId);
        };
    }

    public static Specification<Sighting> dateFrom(LocalDate from) {
        return (root, query, cb) -> {
            if (from == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("sightingDate"), from);
        };
    }

    public static Specification<Sighting> dateTo(LocalDate to) {
        return (root, query, cb) -> {
            if (to == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("sightingDate"), to);
        };
    }

    public static Specification<Sighting> isFavorite(Boolean favorite) {
        return (root, query, cb) -> {
            if (favorite == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("favorite"), favorite);
        };
    }

    public static Specification<Sighting> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("notes")), pattern),
                    cb.like(cb.lower(root.get("observerName")), pattern),
                    cb.like(cb.lower(root.get("bird").get("commonName")), pattern),
                    cb.like(cb.lower(root.get("location").get("name")), pattern)
            );
        };
    }
}
