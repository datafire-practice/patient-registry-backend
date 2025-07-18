package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.exception.GlobalExceptionHandler;
import com.medical.registry_backend.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @GetMapping
    public ResponseEntity<Page<Patient>> getAllPatients(Pageable pageable) {
        logger.info("Fetching all patients with pageable: {}", pageable);
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        logger.info("Fetching patient with ID: {}", id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        logger.info("Creating patient: {}", patient);
        Patient savedPatient = patientService.savePatient(patient);
        logger.info("Created patient with ID: {}", savedPatient.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        logger.info("Updating patient with ID: {}", id);
        try {
            Patient updatedPatient = patientService.updatePatient(id, patient);
            logger.info("Updated patient with ID: {}", updatedPatient.getId());
            return ResponseEntity.ok(updatedPatient);
        } catch (ValidationException e) {
            logger.error("Validation error while updating patient with ID: {}", id, e);
            return ResponseEntity.badRequest().body(new GlobalExceptionHandler.ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        logger.info("Deleting patient with ID: {}", id);
        patientService.deletePatient(id);
        logger.info("Deleted patient with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}