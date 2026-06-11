package com.featherlog.api.repository;

import com.featherlog.api.model.HabitatType;
import com.featherlog.api.model.Location;
import org.springframework.data.jpa.domain.Specification;

public final class LocationSpecifications {

    private LocationSpecifications() {
    }

    public static Specification<Location> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("country")), pattern),
                    cb.like(cb.lower(root.get("region")), pattern)
            );
        };
    }

    public static Specification<Location> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("country")), country.toLowerCase());
        };
    }

    public static Specification<Location> hasHabitatType(HabitatType habitatType) {
        return (root, query, cb) -> {
            if (habitatType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("habitatType"), habitatType);
        };
    }
}
