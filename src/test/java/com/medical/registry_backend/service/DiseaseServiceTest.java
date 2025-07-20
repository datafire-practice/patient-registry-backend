package com.medical.registry_backend.service;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.entity.Mkb10;
import com.medical.registry_backend.entity.Patient;
import com.medical.registry_backend.repository.DiseaseRepository;
import com.medical.registry_backend.repository.Mkb10Repository;
import com.medical.registry_backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiseaseServiceTest {

    @Mock
    private DiseaseRepository diseaseRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private Mkb10Repository mkb10Repository;

    @InjectMocks
    private DiseaseServiceImpl diseaseService;

    private Patient patient;
    private Disease disease;
    private Mkb10 mkb10;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setLastName("Иванов");
        patient.setFirstName("Иван");
        patient.setMiddleName("Иванович");

        mkb10 = new Mkb10();
        mkb10.setCode("A00.0");
        mkb10.setName("Холера");

        disease = new Disease();
        disease.setId(1L);
        disease.setPatient(patient);
        disease.setMkb10(mkb10);
        disease.setStartDate(LocalDate.of(2023, 1, 1));
        disease.setEndDate(LocalDate.of(2023, 1, 10));
        disease.setPrescriptions("Лечение");
        disease.setSickLeaveIssued(true);
    }

    @Test
    void saveDisease_validDisease_shouldSaveAndReturnDisease() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(mkb10Repository.findById("A00.0")).thenReturn(Optional.of(mkb10));
        when(diseaseRepository.save(any(Disease.class))).thenReturn(disease);

        Disease result = diseaseService.saveDisease(1L, disease);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("A00.0", result.getMkb10().getCode());
        assertEquals(patient, result.getPatient());
        verify(patientRepository).findById(1L);
        verify(mkb10Repository).findById("A00.0");
        verify(diseaseRepository).save(any(Disease.class));
    }

    @Test
    void saveDisease_patientNotFound_shouldThrowEntityNotFoundException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.saveDisease(1L, disease);
        });

        assertEquals("Пациент с ID 1 не найден", exception.getMessage());
        verify(patientRepository).findById(1L);
        verify(mkb10Repository, never()).findById(any());
        verify(diseaseRepository, never()).save(any());
    }

    @Test
    void saveDisease_mkb10NotFound_shouldThrowEntityNotFoundException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(mkb10Repository.findById("A00.0")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.saveDisease(1L, disease);
        });

        assertEquals("Код МКБ-10 A00.0 не найден", exception.getMessage());
        verify(patientRepository).findById(1L);
        verify(mkb10Repository).findById("A00.0");
        verify(diseaseRepository, never()).save(any());
    }

    @Test
    void updateDisease_validDisease_shouldUpdateAndReturnDisease() {
        Disease updatedDisease = new Disease();
        updatedDisease.setMkb10(new Mkb10());
        updatedDisease.getMkb10().setCode("B02.0");
        updatedDisease.setStartDate(LocalDate.of(2023, 2, 1));
        updatedDisease.setEndDate(LocalDate.of(2023, 2, 10));
        updatedDisease.setPrescriptions("Новое лечение");
        updatedDisease.setSickLeaveIssued(false);

        Mkb10 updatedMkb10 = new Mkb10();
        updatedMkb10.setCode("B02.0");
        updatedMkb10.setName("Грипп");

        Disease expectedDisease = new Disease();
        expectedDisease.setId(1L);
        expectedDisease.setPatient(patient);
        expectedDisease.setMkb10(updatedMkb10);
        expectedDisease.setStartDate(LocalDate.of(2023, 2, 1));
        expectedDisease.setEndDate(LocalDate.of(2023, 2, 10));
        expectedDisease.setPrescriptions("Новое лечение");
        expectedDisease.setSickLeaveIssued(false);

        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(mkb10Repository.findById("B02.0")).thenReturn(Optional.of(updatedMkb10));
        when(diseaseRepository.save(any(Disease.class))).thenReturn(expectedDisease);

        Disease result = diseaseService.updateDisease(1L, 1L, updatedDisease);

        assertNotNull(result);
        assertEquals("B02.0", result.getMkb10().getCode());
        assertEquals(LocalDate.of(2023, 2, 1), result.getStartDate());
        assertEquals("Новое лечение", result.getPrescriptions());
        assertFalse(result.isSickLeaveIssued());
        verify(diseaseRepository).findById(1L);
        verify(mkb10Repository).findById("B02.0");
        verify(diseaseRepository).save(any(Disease.class));
    }

    @Test
    void updateDisease_diseaseNotFound_shouldThrowEntityNotFoundException() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.updateDisease(1L, 1L, disease);
        });

        assertEquals("Заболевание с ID 1 не найдено", exception.getMessage());
        verify(diseaseRepository).findById(1L);
        verify(mkb10Repository, never()).findById(any());
        verify(diseaseRepository, never()).save(any());
    }

    @Test
    void updateDisease_wrongPatientId_shouldThrowIllegalArgumentException() {
        disease.getPatient().setId(2L);
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            diseaseService.updateDisease(1L, 1L, disease);
        });

        assertEquals("Заболевание не принадлежит пациенту с ID 1", exception.getMessage());
        verify(diseaseRepository).findById(1L);
        verify(mkb10Repository, never()).findById(any());
        verify(diseaseRepository, never()).save(any());
    }

    @Test
    void updateDisease_mkb10NotFound_shouldThrowEntityNotFoundException() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(mkb10Repository.findById("A00.0")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.updateDisease(1L, 1L, disease);
        });

        assertEquals("Код МКБ-10 A00.0 не найден", exception.getMessage());
        verify(diseaseRepository).findById(1L);
        verify(mkb10Repository).findById("A00.0");
        verify(diseaseRepository, never()).save(any());
    }

    @Test
    void updateDisease_saveError_shouldThrowRuntimeException() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(mkb10Repository.findById("A00.0")).thenReturn(Optional.of(mkb10));
        when(diseaseRepository.save(any(Disease.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            diseaseService.updateDisease(1L, 1L, disease);
        });

        assertEquals("Ошибка при обновлении заболевания с ID 1", exception.getMessage());
        verify(diseaseRepository).findById(1L);
        verify(mkb10Repository).findById("A00.0");
        verify(diseaseRepository).save(any(Disease.class));
    }

    @Test
    void getDiseaseById_validIds_shouldReturnDisease() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        Disease result = diseaseService.getDiseaseById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("A00.0", result.getMkb10().getCode());
        verify(diseaseRepository).findById(1L);
    }

    @Test
    void getDiseaseById_diseaseNotFound_shouldThrowEntityNotFoundException() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.getDiseaseById(1L, 1L);
        });

        assertEquals("Заболевание с ID 1 не найдено", exception.getMessage());
        verify(diseaseRepository).findById(1L);
    }

    @Test
    void getDiseaseById_wrongPatientId_shouldThrowIllegalArgumentException() {
        disease.getPatient().setId(2L);
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            diseaseService.getDiseaseById(1L, 1L);
        });

        assertEquals("Заболевание не принадлежит пациенту с ID 1", exception.getMessage());
        verify(diseaseRepository).findById(1L);
    }

    @Test
    void getDiseasesByPatientId_validPatientId_shouldReturnPagedDiseases() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate"));
        List<Disease> diseases = Collections.singletonList(disease);
        Page<Disease> page = new PageImpl<>(diseases, pageable, diseases.size());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diseaseRepository.findByPatientId(1L, pageable)).thenReturn(page);

        Page<Disease> result = diseaseService.getDiseasesByPatientId(1L, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("A00.0", result.getContent().get(0).getMkb10().getCode());
        verify(patientRepository).findById(1L);
        verify(diseaseRepository).findByPatientId(1L, pageable);
    }

    @Test
    void getDiseasesByPatientId_patientNotFound_shouldThrowEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate"));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.getDiseasesByPatientId(1L, pageable);
        });

        assertEquals("Пациент с ID 1 не найден", exception.getMessage());
        verify(patientRepository).findById(1L);
        verify(diseaseRepository, never()).findByPatientId(anyLong(), any());
    }

    @Test
    void getDiseasesByPatientId_noDiseases_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate"));
        Page<Disease> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diseaseRepository.findByPatientId(1L, pageable)).thenReturn(emptyPage);

        Page<Disease> result = diseaseService.getDiseasesByPatientId(1L, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(patientRepository).findById(1L);
        verify(diseaseRepository).findByPatientId(1L, pageable);
    }

    @Test
    void deleteDisease_validIds_shouldDeleteDisease() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        diseaseService.deleteDisease(1L, 1L);

        verify(diseaseRepository).findById(1L);
        verify(diseaseRepository).delete(disease);
    }

    @Test
    void deleteDisease_diseaseNotFound_shouldThrowEntityNotFoundException() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            diseaseService.deleteDisease(1L, 1L);
        });

        assertEquals("Заболевание с ID 1 не найдено", exception.getMessage());
        verify(diseaseRepository).findById(1L);
        verify(diseaseRepository, never()).delete(any());
    }

    @Test
    void deleteDisease_wrongPatientId_shouldThrowIllegalArgumentException() {
        disease.getPatient().setId(2L);
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            diseaseService.deleteDisease(1L, 1L);
        });

        assertEquals("Заболевание не принадлежит пациенту с ID 1", exception.getMessage());
        verify(diseaseRepository).findById(1L);
        verify(diseaseRepository, never()).delete(any());
    }
}