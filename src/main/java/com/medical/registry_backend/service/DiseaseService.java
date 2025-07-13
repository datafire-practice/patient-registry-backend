package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.repository.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseService {
    private final DiseaseRepository diseaseRepository;

    // Получить заболевания пациента
    public List<Disease> getDiseasesByPatientId(Long patientId) {
        return diseaseRepository.findByPatientId(patientId);
    }

    // Сохранить заболевание
    public Disease saveDisease(Disease disease) {
        return diseaseRepository.save(disease);
    }

    // Удалить заболевание
    @Transactional
    public void deleteDisease(Long id) {
        diseaseRepository.deleteById(id);
    }
}