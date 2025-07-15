package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Disease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Page<Disease> findByPatientId(Long patientId, Pageable pageable);
    List<Disease> findByPatientId(Long patientId);
}