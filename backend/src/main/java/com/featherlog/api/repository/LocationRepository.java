package com.featherlog.api.repository;

import com.featherlog.api.dto.CountryOption;
import com.featherlog.api.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {

    @Query("SELECT DISTINCT new com.featherlog.api.dto.CountryOption(l.country, l.countryCode) " +
            "FROM Location l ORDER BY l.country ASC")
    List<CountryOption> findDistinctCountries();
}
