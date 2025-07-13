package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.repository.DiseaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseServiceImpl implements DiseaseService {
    private final DiseaseRepository diseaseRepository;

    @Override
    public List<Disease> getDiseasesByPatientId(Long patientId) {
        return diseaseRepository.findByPatientId(patientId);
    }

    @Override
    public Disease saveDisease(Disease disease) {
        validateDisease(disease);
        return diseaseRepository.save(disease);
    }

    @Override
    public void deleteDisease(Long diseaseId) {
        diseaseRepository.deleteById(diseaseId);
    }

    private void validateDisease(Disease disease) {
        if (disease.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата начала болезни не должна быть в будущем");
        }
        if (disease.getEndDate() != null && disease.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата окончания болезни не должна быть в будущем");
        }
        if (disease.getPrescriptions().length() > 1024) {
            throw new IllegalArgumentException("Назначения не должны превышать 1024 символа");
        }
    }
}