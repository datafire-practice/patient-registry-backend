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
    public Patient updatePatient(Long id, Patient patient) {
        Patient existingPatient = getPatientById(id);
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setMiddleName(patient.getMiddleName());
        existingPatient.setGender(patient.getGender());
        existingPatient.setBirthDate(patient.getBirthDate());
        existingPatient.setInsuranceNumber(patient.getInsuranceNumber());
        validatePatient(existingPatient);
        return patientRepository.save(existingPatient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Пациент с ID " + id + " не найден");
        }
        patientRepository.deleteById(id);
    }

    private void validatePatient(Patient patient) {
        if (patient.getLastName() == null || !patient.getLastName().matches("^[А-Яа-я\\s-]*$")) {
            throw new IllegalArgumentException("Фамилия должна содержать только кириллицу, пробел и дефис");
        }
        if (patient.getFirstName() == null || !patient.getFirstName().matches("^[А-Яа-я\\s-]*$")) {
            throw new IllegalArgumentException("Имя должно содержать только кириллицу, пробел и дефис");
        }
        if (patient.getMiddleName() != null && !patient.getMiddleName().matches("^[А-Яа-я\\s-]*$")) {
            throw new IllegalArgumentException("Отчество должно содержать только кириллицу, пробел и дефис");
        }
        if (patient.getGender() == null || !patient.getGender().matches("^[МЖ]$")) {
            throw new IllegalArgumentException("Пол должен быть 'М' или 'Ж'");
        }
        if (patient.getBirthDate() == null || patient.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не должна быть в будущем");
        }
        if (patient.getInsuranceNumber() == null || !patient.getInsuranceNumber().matches("\\d{16}")) {
            throw new IllegalArgumentException("Номер полиса должен содержать 16 цифр");
        }
    }
}