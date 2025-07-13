package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    @Override
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пациент с ID " + id + " не найден"));
    }

    @Override
    public Patient savePatient(Patient patient) {
        validatePatient(patient);
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    private void validatePatient(Patient patient) {
        if (patient.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не должна быть в будущем");
        }
        if (patient.getInsuranceNumber() == null || !patient.getInsuranceNumber().matches("\\d{16}")) {
            throw new IllegalArgumentException("Номер полиса должен содержать 16 цифр");
        }
    }
}