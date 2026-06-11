package com.featherlog.api.repository;

import com.featherlog.api.model.Bird;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BirdRepository extends JpaRepository<Bird, Long>, JpaSpecificationExecutor<Bird> {

    boolean existsByScientificNameIgnoreCase(String scientificName);

    @Query("SELECT DISTINCT b.family FROM Bird b ORDER BY b.family ASC")
    List<String> findDistinctFamilies();
}
