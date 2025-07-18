package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiseaseService {
    Disease createDisease(Long patientId, Disease disease);
    Disease updateDisease(Long patientId, Long diseaseId, Disease disease);
    Disease getDiseaseById(Long patientId, Long diseaseId);
    Page<Disease> getAllDiseases(Long patientId, Pageable pageable);
    void deleteDisease(Long patientId, Long diseaseId);
}