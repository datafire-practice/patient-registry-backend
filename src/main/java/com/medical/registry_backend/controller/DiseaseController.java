package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/diseases")
@RequiredArgsConstructor
public class DiseaseController {
    private final DiseaseService diseaseService;

    // Получить заболевания пациента
    @GetMapping
    public List<Disease> getDiseasesByPatientId(@PathVariable Long patientId) {
        return diseaseService.getDiseasesByPatientId(patientId);
    }

    // Добавить заболевание
    @PostMapping
    public Disease addDisease(@PathVariable Long patientId, @RequestBody Disease disease) {
        return diseaseService.saveDisease(disease);
    }

    // Удалить заболевание
    @DeleteMapping("/{diseaseId}")
    public void deleteDisease(@PathVariable Long diseaseId) {
        diseaseService.deleteDisease(diseaseId);
    }
}