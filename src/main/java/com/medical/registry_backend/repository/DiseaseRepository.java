package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Disease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    @EntityGraph(attributePaths = {"mkb10"})
    Page<Disease> findByPatientId(Long patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"mkb10"})
    Optional<Disease> findById(Long id);
}