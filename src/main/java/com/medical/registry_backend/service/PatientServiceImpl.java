package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    @Override
    public Page<Patient> getAllPatients(Pageable pageable) {
        logger.info("Fetching patients with pageable: {}", pageable);
        Page<Patient> page = patientRepository.findAll(pageable);
        if (page.isEmpty()) {
            logger.warn("No patients found for pageable: {}", pageable);
        } else {
            page.getContent().forEach(this::initializePatient);
        }
        return page;
    }

    @Override
    public List<Patient> getAllPatients() {
        logger.info("Fetching all patients as list");
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            logger.warn("No patients found");
        } else {
            patients.forEach(this::initializePatient);
        }
        return patients;
    }

    @Override
    public Patient getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пациент с ID " + id + " не найден"));
        initializePatient(patient);
        return patient;
    }

    @Override
    public Patient savePatient(Patient patient) {
        try {
            // Устанавливаем связь patient для каждого disease
            if (patient.getDiseases() != null) {
                for (Disease disease : patient.getDiseases()) {
                    disease.setPatient(patient);
                }
            }
            Patient savedPatient = patientRepository.save(patient);
            initializePatient(savedPatient);
            logger.info("Saved patient with ID: {}", savedPatient.getId());
            return savedPatient;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while saving patient: {}", patient, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data integrity violation");
        }
    }

    @Override
    public Patient updatePatient(Long id, Patient patient) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setMiddleName(patient.getMiddleName());
        existingPatient.setGender(patient.getGender());
        existingPatient.setBirthDate(patient.getBirthDate());
        existingPatient.setInsuranceNumber(patient.getInsuranceNumber());
        // Обновляем diseases
        if (patient.getDiseases() != null) {
            existingPatient.getDiseases().clear();
            for (Disease disease : patient.getDiseases()) {
                disease.setPatient(existingPatient);
                existingPatient.getDiseases().add(disease);
            }
        }
        try {
            Patient updatedPatient = patientRepository.save(existingPatient);
            initializePatient(updatedPatient);
            logger.info("Updated patient with ID: {}", updatedPatient.getId());
            return updatedPatient;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while updating patient with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data integrity violation");
        }
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent patient with ID: {}", id);
            throw new EntityNotFoundException("Пациент с ID " + id + " не найден");
        }
        patientRepository.deleteById(id);
        logger.info("Deleted patient with ID: {}", id);
    }

    @Override
    public void deleteAll() {
        patientRepository.deleteAll();
        logger.info("Deleted all patients");
    }

    private void initializePatient(Patient patient) {
        Hibernate.initialize(patient.getDiseases());
        for (Disease disease : patient.getDiseases()) {
            Hibernate.initialize(disease.getMkb10());
        }
    }
}