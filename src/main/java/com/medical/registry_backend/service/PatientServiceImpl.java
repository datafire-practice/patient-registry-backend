package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
        }
        return page;
    }

    @Override
    public List<Patient> getAllPatients() {
        logger.info("Fetching all patients as list");
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            logger.warn("No patients found");
        }
        return patients;
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пациент с ID " + id + " не найден"));
    }

    @Override
    public Patient savePatient(Patient patient) {
        try {
            return patientRepository.save(patient);
        } catch (DataIntegrityViolationException e) {
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
        try {
            return patientRepository.save(existingPatient);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data integrity violation");
        }
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Пациент с ID " + id + " не найден");
        }
        patientRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        patientRepository.deleteAll();
    }
}