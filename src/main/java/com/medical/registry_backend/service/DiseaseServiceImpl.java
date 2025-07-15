package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.DiseaseRepository;
import com.medical.registry_backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DiseaseServiceImpl implements DiseaseService {
    private final DiseaseRepository diseaseRepository;
    private final PatientRepository patientRepository;

    @Override
    public Page<Disease> getDiseasesByPatientId(Long patientId, Pageable pageable) {
        return diseaseRepository.findByPatientId(patientId, pageable);
    }

    @Override
    public Disease getDiseaseById(Long diseaseId) {
        return diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new EntityNotFoundException("Заболевание с ID " + diseaseId + " не найдено"));
    }

    @Override
    public Disease saveDisease(Long patientId, Disease disease) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент с ID " + patientId + " не найден"));
        disease.setPatient(patient);
        validateDisease(disease);
        return diseaseRepository.save(disease);
    }

    @Override
    public Disease updateDisease(Long patientId, Long diseaseId, Disease disease) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Пациент с ID " + patientId + " не найден"));
        Disease existingDisease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new EntityNotFoundException("Заболевание с ID " + diseaseId + " не найдено"));
        existingDisease.setMkb10(disease.getMkb10());
        existingDisease.setStartDate(disease.getStartDate());
        existingDisease.setEndDate(disease.getEndDate());
        existingDisease.setPrescriptions(disease.getPrescriptions());
        existingDisease.setSickLeaveIssued(disease.isSickLeaveIssued()); // Исправлено: getSickLeaveIssued -> isSickLeaveIssued
        validateDisease(existingDisease);
        return diseaseRepository.save(existingDisease);
    }

    @Override
    public void deleteDisease(Long diseaseId) {
        if (!diseaseRepository.existsById(diseaseId)) {
            throw new EntityNotFoundException("Заболевание с ID " + diseaseId + " не найдено");
        }
        diseaseRepository.deleteById(diseaseId);
    }

    private void validateDisease(Disease disease) {
        if (disease.getStartDate() == null || disease.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата начала болезни не должна быть в будущем");
        }
        if (disease.getEndDate() != null && disease.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата окончания болезни не должна быть в будущем");
        }
        if (disease.getPrescriptions() == null || disease.getPrescriptions().length() > 1024) {
            throw new IllegalArgumentException("Назначения не должны превышать 1024 символа или быть null");
        }
        if (disease.getMkb10() == null || disease.getMkb10().getCode() == null) {
            throw new IllegalArgumentException("Код МКБ-10 обязателен");
        }
    }


}