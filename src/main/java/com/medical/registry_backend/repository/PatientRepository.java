package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    @EntityGraph(attributePaths = {"diseases", "diseases.mkb10"})
    Optional<Patient> findById(Long id);

    @EntityGraph(attributePaths = {"diseases", "diseases.mkb10"})
    Page<Patient> findAll(Pageable pageable);
}