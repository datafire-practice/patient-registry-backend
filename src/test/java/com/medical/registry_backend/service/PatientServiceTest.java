//package com.medical.registry_backend.service;
//
//import com.medical.registry_backend.entity.Disease;
//import com.medical.registry_backend.entity.Mkb10;
//import com.medical.registry_backend.entity.Patient;
//import com.medical.registry_backend.repository.PatientRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PatientServiceTest {
//
//    @Mock
//    private PatientRepository patientRepository;
//
//    @InjectMocks
//    private PatientServiceImpl patientService;
//
//    private Patient patient;
//    private Patient patient2;
//    private Disease disease;
//
//    @BeforeEach
//    void setUp() {
//        patient = new Patient();
//        patient.setId(1L);
//        patient.setLastName("Иванов");
//        patient.setFirstName("Иван");
//        patient.setMiddleName("Иванович");
//        patient.setGender("М");
//        patient.setBirthDate(LocalDate.of(1990, 1, 1));
//        patient.setInsuranceNumber("1234567890");
//
//        patient2 = new Patient();
//        patient2.setId(2L);
//        patient2.setLastName("Петров");
//        patient2.setFirstName("Петр");
//        patient2.setMiddleName("Петрович");
//        patient2.setGender("М");
//        patient2.setBirthDate(LocalDate.of(1985, 2, 2));
//        patient2.setInsuranceNumber("0987654321");
//
//        disease = new Disease();
//        disease.setMkb10(new Mkb10());
//        disease.getMkb10().setCode("A00.0");
//        disease.setStartDate(LocalDate.now());
//        disease.setPrescriptions("Лечение");
//        disease.setSickLeaveIssued(true);
//
//        patient.setDiseases(new ArrayList<>(Collections.singletonList(disease)));
//    }
//
//    @Test
//    void getAllPatients_pageable_shouldReturnPagedPatients() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
//        List<Patient> patients = Arrays.asList(patient, patient2);
//        Page<Patient> page = new PageImpl<>(patients, pageable, patients.size());
//        when(patientRepository.findAll(pageable)).thenReturn(page);
//
//        Page<Patient> result = patientService.getAllPatients(pageable);
//
//        assertEquals(2, result.getContent().size());
//        assertEquals("Иванов", result.getContent().get(0).getLastName());
//        assertEquals("Петров", result.getContent().get(1).getLastName());
//        verify(patientRepository).findAll(pageable);
//    }
//
//    @Test
//    void getAllPatients_pageable_emptyPage_shouldReturnEmptyPage() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
//        Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
//        when(patientRepository.findAll(pageable)).thenReturn(emptyPage);
//
//        Page<Patient> result = patientService.getAllPatients(pageable);
//
//        assertTrue(result.getContent().isEmpty());
//        verify(patientRepository).findAll(pageable);
//    }
//
//    @Test
//    void getAllPatients_shouldReturnAllPatients() {
//        List<Patient> patients = Arrays.asList(patient, patient2);
//        when(patientRepository.findAll()).thenReturn(patients);
//
//        List<Patient> result = patientService.getAllPatients();
//
//        assertEquals(2, result.size());
//        assertEquals("Иванов", result.get(0).getLastName());
//        assertEquals("Петров", result.get(1).getLastName());
//        verify(patientRepository).findAll();
//    }
//
//    @Test
//    void getAllPatients_emptyList_shouldReturnEmptyList() {
//        when(patientRepository.findAll()).thenReturn(Collections.emptyList());
//
//        List<Patient> result = patientService.getAllPatients();
//
//        assertTrue(result.isEmpty());
//        verify(patientRepository).findAll();
//    }
//
//    @Test
//    void getPatientById_shouldReturnPatient() {
//        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
//
//        Patient result = patientService.getPatientById(1L);
//
//        assertNotNull(result);
//        assertEquals("Иванов", result.getLastName());
//        verify(patientRepository).findById(1L);
//    }
//
//    @Test
//    void getPatientById_notFound_shouldThrowEntityNotFoundException() {
//        when(patientRepository.findById(3L)).thenReturn(Optional.empty());
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//            patientService.getPatientById(3L);
//        });
//
//        assertEquals("Пациент с ID 3 не найден", exception.getMessage());
//        verify(patientRepository).findById(3L);
//    }
//
//    @Test
//    void savePatient_validPatient_shouldSaveAndReturnPatient() {
//        when(patientRepository.save(patient)).thenReturn(patient);
//
//        Patient result = patientService.savePatient(patient);
//
//        assertNotNull(result);
//        assertEquals("Иванов", result.getLastName());
//        assertEquals(1, result.getDiseases().size());
//        assertEquals(patient, result.getDiseases().get(0).getPatient());
//        verify(patientRepository).save(patient);
//    }
//
//    @Test
//    void savePatient_dataIntegrityViolation_shouldThrowResponseStatusException() {
//        when(patientRepository.save(patient)).thenThrow(new DataIntegrityViolationException("Constraint violation"));
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
//            patientService.savePatient(patient);
//        });
//
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
//        assertEquals("Data integrity violation", exception.getReason());
//        verify(patientRepository).save(patient);
//    }
//
//    @Test
//    void savePatient_invalidDisease_noMkb10_shouldThrowIllegalArgumentException() {
//        Disease invalidDisease = new Disease();
//        invalidDisease.setStartDate(LocalDate.now());
//        invalidDisease.setPrescriptions("Лечение");
//        invalidDisease.setSickLeaveIssued(true);
//        patient.setDiseases(new ArrayList<>(Collections.singletonList(invalidDisease)));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            patientService.savePatient(patient);
//        });
//
//        assertEquals("Disease must have an associated Mkb10", exception.getMessage());
//        verify(patientRepository, never()).save(any());
//    }
//
//    @Test
//    void savePatient_invalidDisease_noStartDate_shouldThrowIllegalArgumentException() {
//        Disease invalidDisease = new Disease();
//        invalidDisease.setMkb10(new Mkb10());
//        invalidDisease.setPrescriptions("Лечение");
//        invalidDisease.setSickLeaveIssued(true);
//        patient.setDiseases(new ArrayList<>(Collections.singletonList(invalidDisease)));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            patientService.savePatient(patient);
//        });
//
//        assertEquals("Disease must have a start date", exception.getMessage());
//        verify(patientRepository, never()).save(any());
//    }
//
//    @Test
//    void savePatient_invalidDisease_noPrescriptions_shouldThrowIllegalArgumentException() {
//        Disease invalidDisease = new Disease();
//        invalidDisease.setMkb10(new Mkb10());
//        invalidDisease.setStartDate(LocalDate.now());
//        invalidDisease.setSickLeaveIssued(true);
//        patient.setDiseases(new ArrayList<>(Collections.singletonList(invalidDisease)));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            patientService.savePatient(patient);
//        });
//
//        assertEquals("Disease must have prescriptions", exception.getMessage());
//        verify(patientRepository, never()).save(any());
//    }
//
//    @Test
//    void savePatient_invalidDisease_noSickLeaveStatus_shouldThrowIllegalArgumentException() {
//        Disease invalidDisease = new Disease();
//        invalidDisease.setMkb10(new Mkb10());
//        invalidDisease.setStartDate(LocalDate.now());
//        invalidDisease.setPrescriptions("Лечение");
//        patient.setDiseases(new ArrayList<>(Collections.singletonList(invalidDisease)));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            patientService.savePatient(patient);
//        });
//
//        assertEquals("Disease must specify sick leave issued status", exception.getMessage());
//        verify(patientRepository, never()).save(any());
//    }
//
//    @Test
//    void updatePatient_validPatient_shouldUpdateAndReturnPatient() {
//        Patient updatedPatient = new Patient();
//        updatedPatient.setLastName("Сидоров");
//        updatedPatient.setFirstName("Сидор");
//        updatedPatient.setMiddleName("Сидорович");
//        updatedPatient.setGender("М");
//        updatedPatient.setBirthDate(LocalDate.of(1995, 3, 3));
//        updatedPatient.setInsuranceNumber("1112223334");
//        updatedPatient.setDiseases(new ArrayList<>(Collections.singletonList(disease)));
//
//        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
//        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
//
//        Patient result = patientService.updatePatient(1L, updatedPatient);
//
//        assertNotNull(result);
//        assertEquals("Сидоров", result.getLastName());
//        assertEquals("Сидор", result.getFirstName());
//        assertEquals(1, result.getDiseases().size());
//        assertEquals(patient, result.getDiseases().get(0).getPatient());
//        verify(patientRepository).findById(1L);
//        verify(patientRepository).save(any(Patient.class));
//    }
//
//    @Test
//    void updatePatient_notFound_shouldThrowResponseStatusException() {
//        when(patientRepository.findById(3L)).thenReturn(Optional.empty());
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
//            patientService.updatePatient(3L, patient);
//        });
//
//        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
//        assertEquals("Patient not found", exception.getReason());
//        verify(patientRepository).findById(3L);
//        verify(patientRepository, never()).save(any());
//    }
//
//    @Test
//    void updatePatient_dataIntegrityViolation_shouldThrowResponseStatusException() {
//        Patient updatedPatient = new Patient();
//        updatedPatient.setLastName("Сидоров");
//        updatedPatient.setFirstName("Сидор");
//        updatedPatient.setMiddleName("Сидорович");
//        updatedPatient.setGender("М");
//        updatedPatient.setBirthDate(LocalDate.of(1995, 3, 3));
//        updatedPatient.setInsuranceNumber("1112223334");
//        updatedPatient.setDiseases(new ArrayList<>(Collections.singletonList(disease)));
//
//        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
//        when(patientRepository.save(any(Patient.class))).thenThrow(new DataIntegrityViolationException("Constraint violation"));
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
//            patientService.updatePatient(1L, updatedPatient);
//        });
//
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
//        assertEquals("Data integrity violation", exception.getReason());
//        verify(patientRepository).findById(1L);
//        verify(patientRepository).save(any(Patient.class));
//    }
//
//    @Test
//    void updatePatient_invalidDisease_noMkb10_shouldThrowIllegalArgumentException() {
//        Disease invalidDisease = new Disease();
//        invalidDisease.setStartDate(LocalDate.now());
//        invalidDisease.setPrescriptions("Лечение");
//        invalidDisease.setSickLeaveIssued(true);
//        Patient updatedPatient = new Patient();
//        updatedPatient.setDiseases(new ArrayList<>(Collections.singletonList(invalidDisease)));
//        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            patientService.updatePatient(1L, updatedPatient);
//        });
//
//        assertEquals("Disease must have an associated Mkb10", exception.getMessage());
//        verify(patientRepository).findById(1L);
//        verify(patientRepository, never()).save(any());
//    }
//
//    @Test
//    void deletePatient_existingId_shouldDeletePatient() {
//        when(patientRepository.existsById(1L)).thenReturn(true);
//
//        patientService.deletePatient(1L);
//
//        verify(patientRepository).existsById(1L);
//        verify(patientRepository).deleteById(1L);
//    }
//
//    @Test
//    void deletePatient_nonExistingId_shouldThrowEntityNotFoundException() {
//        when(patientRepository.existsById(3L)).thenReturn(false);
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//            patientService.deletePatient(3L);
//        });
//
//        assertEquals("Пациент с ID 3 не найден", exception.getMessage());
//        verify(patientRepository).existsById(3L);
//        verify(patientRepository, never()).deleteById(any());
//    }
//
//    @Test
//    void deleteAll_shouldDeleteAllPatients() {
//        patientService.deleteAll();
//
//        verify(patientRepository).deleteAll();
//    }
//}