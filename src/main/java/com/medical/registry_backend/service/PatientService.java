package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    // Получить всех пациентов с пагинацией
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    // Получить пациента по ID
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow();
    }

    // Сохранить пациента
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Удалить пациента
    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}