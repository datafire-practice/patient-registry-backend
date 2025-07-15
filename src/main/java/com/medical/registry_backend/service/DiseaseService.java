package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiseaseService {
    Page<Disease> getDiseasesByPatientId(Long patientId, Pageable pageable);
    Disease getDiseaseById(Long diseaseId);
    Disease saveDisease(Long patientId, Disease disease);
    Disease updateDisease(Long patientId, Long diseaseId, Disease disease);
    void deleteDisease(Long diseaseId);
}