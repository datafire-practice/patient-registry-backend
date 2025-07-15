package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.exception.GlobalExceptionHandler;
import com.medical.registry_backend.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<Page<Patient>> getAllPatients(Pageable pageable) {
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.savePatient(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        try {
            Patient updatedPatient = patientService.updatePatient(id, patient);
            return ResponseEntity.ok(updatedPatient);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new GlobalExceptionHandler.ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}