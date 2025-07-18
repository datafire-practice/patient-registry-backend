package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.DiseaseRepository;
import com.medical.registry_backend.repository.Mkb10Repository;
import com.medical.registry_backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DiseaseServiceImpl implements DiseaseService {
    private static final Logger logger = LoggerFactory.getLogger(DiseaseServiceImpl.class);

    private final DiseaseRepository diseaseRepository;
    private final PatientRepository patientRepository;
    private final Mkb10Repository mkb10Repository;

    public DiseaseServiceImpl(DiseaseRepository diseaseRepository, PatientRepository patientRepository, Mkb10Repository mkb10Repository) {
        this.diseaseRepository = diseaseRepository;
        this.patientRepository = patientRepository;
        this.mkb10Repository = mkb10Repository;
    }

    @Override
    public Disease createDisease(Long patientId, Disease disease) {
        logger.info("Creating disease for patient ID: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    logger.error("Patient with ID {} not found", patientId);
                    return new EntityNotFoundException("Пациент с ID " + patientId + " не найден");
                });

        Mkb10 mkb10 = mkb10Repository.findById(disease.getMkb10().getCode())
                .orElseThrow(() -> {
                    logger.error("Mkb10 code {} not found", disease.getMkb10().getCode());
                    return new EntityNotFoundException("Код МКБ-10 " + disease.getMkb10().getCode() + " не найден");
                });
        logger.debug("Found Mkb10: {}", mkb10);

        disease.setPatient(patient);
        disease.setMkb10(mkb10);
        Disease savedDisease = diseaseRepository.save(disease);
        logger.info("Saved disease with ID: {}", savedDisease.getId());
        return savedDisease;
    }

    @Override
    public Disease updateDisease(Long patientId, Long diseaseId, Disease disease) {
        logger.info("Updating disease ID: {} for patient ID: {}", diseaseId, patientId);
        Disease existingDisease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> {
                    logger.error("Disease with ID {} not found", diseaseId);
                    return new EntityNotFoundException("Заболевание с ID " + diseaseId + " не найдено");
                });

        if (!existingDisease.getPatient().getId().equals(patientId)) {
            logger.error("Disease ID {} does not belong to patient ID {}", diseaseId, patientId);
            throw new IllegalArgumentException("Заболевание не принадлежит пациенту с ID " + patientId);
        }

        Mkb10 mkb10 = mkb10Repository.findById(disease.getMkb10().getCode())
                .orElseThrow(() -> {
                    logger.error("Mkb10 code {} not found", disease.getMkb10().getCode());
                    return new EntityNotFoundException("Код МКБ-10 " + disease.getMkb10().getCode() + " не найден");
                });
        logger.debug("Found Mkb10: {}", mkb10);

        existingDisease.setMkb10(mkb10);
        existingDisease.setStartDate(disease.getStartDate());
        existingDisease.setEndDate(disease.getEndDate());
        existingDisease.setPrescriptions(disease.getPrescriptions());
        existingDisease.setSickLeaveIssued(disease.getSickLeaveIssued());

        Disease updatedDisease = diseaseRepository.save(existingDisease);
        logger.info("Updated disease with ID: {}", updatedDisease.getId());
        return updatedDisease;
    }

    @Override
    public Disease getDiseaseById(Long patientId, Long diseaseId) {
        logger.info("Fetching disease ID: {} for patient ID: {}", diseaseId, patientId);
        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> {
                    logger.error("Disease with ID {} not found", diseaseId);
                    return new EntityNotFoundException("Заболевание с ID " + diseaseId + " не найдено");
                });

        if (!disease.getPatient().getId().equals(patientId)) {
            logger.error("Disease ID {} does not belong to patient ID {}", diseaseId, patientId);
            throw new IllegalArgumentException("Заболевание не принадлежит пациенту с ID " + patientId);
        }

        logger.info("Found disease with ID: {}", diseaseId);
        return disease;
    }

    @Override
    public Page<Disease> getAllDiseases(Long patientId, Pageable pageable) {
        logger.info("Fetching all diseases for patient ID: {} with pageable: {}", patientId, pageable);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    logger.error("Patient with ID {} not found", patientId);
                    return new EntityNotFoundException("Пациент с ID " + patientId + " не найден");
                });

        Page<Disease> diseases = diseaseRepository.findByPatientId(patientId, pageable);
        logger.info("Found {} diseases for patient ID: {}", diseases.getTotalElements(), patientId);
        return diseases;
    }

    @Override
    public void deleteDisease(Long patientId, Long diseaseId) {
        logger.info("Deleting disease ID: {} for patient ID: {}", diseaseId, patientId);
        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> {
                    logger.error("Disease with ID {} not found", diseaseId);
                    return new EntityNotFoundException("Заболевание с ID " + diseaseId + " не найдено");
                });

        if (!disease.getPatient().getId().equals(patientId)) {
            logger.error("Disease ID {} does not belong to patient ID {}", diseaseId, patientId);
            throw new IllegalArgumentException("Заболевание не принадлежит пациенту с ID " + patientId);
        }

        diseaseRepository.delete(disease);
        logger.info("Deleted disease with ID: {}", diseaseId);
    }
}