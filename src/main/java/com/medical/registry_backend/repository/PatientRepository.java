package com.medical.registry_backend.repository;

import com.medical.registry_backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Patient findByInsuranceNumber(String insuranceNumber);
}