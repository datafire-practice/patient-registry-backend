package com.medical.registry_backend.controller;

import com.medical.registry_backend.entity.Disease;
import com.medical.registry_backend.service.DiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient/{patient_id}/disease")
@RequiredArgsConstructor
public class DiseaseController {
    private final DiseaseService diseaseService;
    private static final Logger logger = LoggerFactory.getLogger(DiseaseController.class);

    @Operation(summary = "Получить список заболеваний пациента с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заболеваний успешно возвращён"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @GetMapping
    public ResponseEntity<Page<Disease>> getDiseasesByPatientId(@PathVariable("patient_id") Long patientId, Pageable pageable) {
        logger.info("Fetching diseases for patient ID: {} with pageable: {}", patientId, pageable);
        return ResponseEntity.ok(diseaseService.getDiseasesByPatientId(patientId, pageable));
    }

    @Operation(summary = "Получить заболевание по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание найдено"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены")
    })
    @GetMapping("/{diseaseId}")
    public ResponseEntity<Disease> getDiseaseById(@PathVariable("patient_id") Long patientId, @PathVariable Long diseaseId) {
        logger.info("Fetching disease ID: {} for patient ID: {}", diseaseId, patientId);
        return ResponseEntity.ok(diseaseService.getDiseaseById(patientId, diseaseId));
    }

    @Operation(summary = "Добавить заболевание пациенту")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заболевание успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @PostMapping
    public ResponseEntity<Disease> addDisease(@PathVariable("patient_id") Long patientId, @Valid @RequestBody Disease disease) {
        logger.info("Creating disease for patient ID: {}", patientId);
        Disease savedDisease = diseaseService.saveDisease(patientId, disease);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDisease);
    }

    @Operation(summary = "Обновить заболевание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заболевание успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены")
    })
    @PutMapping("/{diseaseId}")
    public ResponseEntity<Disease> updateDisease(@PathVariable("patient_id") Long patientId,
                                                 @PathVariable Long diseaseId,
                                                 @Valid @RequestBody Disease disease) {
        logger.info("Received PUT request to update disease with ID {} for patient ID {}",
                diseaseId, patientId);
        try {
            Disease updatedDisease = diseaseService.updateDisease(patientId, diseaseId, disease);
            logger.info("Returning updated disease with ID {}", diseaseId);
            return ResponseEntity.ok(updatedDisease);
        } catch (Exception e) {
            logger.error("Error updating disease with ID {} for patient ID {}: {}",
                    diseaseId, patientId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Удалить заболевание")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заболевание успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Заболевание или пациент не найдены")
    })
    @DeleteMapping("/{diseaseId}")
    public ResponseEntity<Void> deleteDisease(@PathVariable("patient_id") Long patientId, @PathVariable Long diseaseId) {
        logger.info("Deleting disease ID: {} for patient ID: {}", diseaseId, patientId);
        diseaseService.deleteDisease(patientId, diseaseId);
        return ResponseEntity.noContent().build();
    }
}