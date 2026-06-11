package com.featherlog.api.repository;

import com.featherlog.api.model.Bird;
import com.featherlog.api.model.ConservationStatus;
import org.springframework.data.jpa.domain.Specification;

public final class BirdSpecifications {

    private BirdSpecifications() {
    }

    public static Specification<Bird> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("commonName")), pattern),
                    cb.like(cb.lower(root.get("scientificName")), pattern),
                    cb.like(cb.lower(root.get("family")), pattern)
            );
        };
    }

    public static Specification<Bird> hasFamily(String family) {
        return (root, query, cb) -> {
            if (family == null || family.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("family")), family.toLowerCase());
        };
    }

    public static Specification<Bird> hasConservationStatus(ConservationStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("conservationStatus"), status);
        };
    }
}
