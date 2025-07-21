package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    Page<Patient> getAllPatients(Pageable pageable);
    Patient getPatientById(Long id);
    Patient savePatient(Patient patient);
    Patient updatePatient(Long id, Patient patient);
    void deletePatient(Long id);
    void deleteAll();
    List<Patient> getAllPatients();

}